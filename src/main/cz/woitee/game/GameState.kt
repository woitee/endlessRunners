package cz.woitee.game

import java.util.*

import cz.woitee.game.undoing.IUndo

import cz.woitee.game.objects.Player
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.game.objects.GameObject
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.actions.abstract.HoldAction
import cz.woitee.game.objects.UndoableUpdateGameObject
import cz.woitee.game.effects.UndoableGameEffect
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.undoing.NoActionUndo
import cz.woitee.game.undoing.UndoFactory
import cz.woitee.geom.Vector2Double
import cz.woitee.geom.Vector2Int
import cz.woitee.utils.MySerializable
import cz.woitee.utils.pools.DefaultUndoListPool
import cz.woitee.utils.reverse
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * A state of the game, contains objects in the game, other statuses and provides methods to advance the game
 * and manipulate it's inner workings.
 *
 * Created by woitee on 13/01/2017.
 */

class GameState(val game: Game, val levelGenerator: LevelGenerator?, var tag: String = "") : MySerializable {
    // parameterless constructor for serialization purposes
    constructor(): this(DummyObjects.createDummyGame(), null)

    var player = Player()
    var gameObjects = arrayListOf<GameObject>(player)
    var updateObjects = arrayListOf<GameObject>(player)
    var grid = Grid2D<GameObject?>(WidthBlocks, HeightBlocks, { null })
    var gridX = 0
    var lastAdvanceTime = game.updateTime
        private set
    var gameTime = 0.0
        private set
    var heldActions = HashMap<HoldAction, Double>()

    var isGameOver = false

    val lastColumnX: Int
        get() = gridX + WidthBlocks
    val lastColumnXpx: Double
        get() = (lastColumnX * BlockWidth).toDouble()
    val allActions: List<GameAction>
        get() = game.gameDescription.allActions

    init {
        player.x = PlayerScreenX
        player.y = BlockHeight.toDouble()
        player.xspeed = game.gameDescription.playerStartingSpeed
        player.gameState = this
        for (i in 0 .. WidthBlocks - 1) {
            addToGrid(SolidBlock(), i, 0)
        }
    }

    internal fun addToGrid(gameObject: GameObject?, x: Int, y:Int) {
        gameObject ?: return

        gameObject.x = ((gridX + x) * BlockWidth).toDouble()
        gameObject.y = (y * BlockHeight).toDouble()
        add(gameObject)
    }
    internal fun add(gameObject: GameObject) {
        gameObjects.add(gameObject)
        if (gameObject.isUpdated)
            updateObjects.add(gameObject)

        gameObject.gameState = this
        grid[
                (gameObject.x / BlockWidth - gridX).toInt(),
                (gameObject.y / BlockHeight).toInt()
                ] = gameObject
    }
    internal fun remove(gameObject: GameObject?) {
        gameObject ?: return
        gameObjects.remove(gameObject)
        if (gameObject.isUpdated)
            updateObjects.remove(gameObject)

        grid[
                (gameObject.x / BlockWidth - gridX).toInt(),
                (gameObject.y / BlockHeight).toInt()
                ] = null
    }
    internal fun addColumn(levelGenerator: LevelGenerator) {
        shiftGrid()
        val column = levelGenerator.generateNextColumn(this)
        for (y in 0..column.lastIndex) {
            addToGrid(column[y], WidthBlocks - 1, y)
        }
    }
    private fun shiftGrid() {
        gridX += 1
        for (y in 0 .. grid.height - 1) {
            val gameObject = grid[0, y]
            gameObject ?: continue
            gameObjects.remove(gameObject)
            if (gameObject.isUpdated)
                updateObjects.remove(gameObject)
        }
        grid.shiftX(1)
        System.gc()
    }

    fun advance(time: Double, scrolling:Boolean = false) {
        lastAdvanceTime = time

        synchronized(gameObjects) {
            for (gameObject in updateObjects) {
                gameObject.update(time)
            }
            for (gameEffect in game.gameDescription.permanentEffects) {
                gameEffect.applyOn(this)
            }
            for (heldAction in heldActions.keys) {
                if (!heldAction.canBeKeptApplyingOn(this)) {
                    heldAction.stopApplyingOn(this)
                }
            }
            gameTime += time

            if (scrolling) {
                if (levelGenerator == null) {
                    println("Level Generator should be set when scrolling!")
                } else {
                    val offset = player.x - PlayerScreenX - (gridX * BlockWidth)
                    val blockOffset = (offset / BlockWidth).toInt()

                    for (i in 1..blockOffset) {
//                        println("GridChange $gridX #GameObjects ${gameObjects.size}")
                        this.addColumn(levelGenerator)
                    }
                }
            }
        }
    }
    fun advanceUndoable(time: Double): IUndo {
        val undoList = DefaultUndoListPool.borrowObject()
//        println("Got object ${DefaultUndoListPool.numActive} ${DefaultUndoListPool.numIdle}")
        updateObjects.map {
            val undo = (it as UndoableUpdateGameObject).undoableUpdate(time)
            if (undo != NoActionUndo)
                undoList.add(undo)
        }
        game.gameDescription.permanentEffects.map {
            val undo = (it as UndoableGameEffect).applyUndoablyOn(this)
            if (undo != NoActionUndo)
                undoList.add(undo)
        }

        gameTime += time
        var stoppedHolding: HashMap<HoldAction, Double>? = null
        for (heldAction in heldActions.keys) {
            if (!heldAction.canBeKeptApplyingOn(this)) {
                if (stoppedHolding == null)
                    stoppedHolding = HashMap<HoldAction, Double>()
                stoppedHolding[heldAction] = heldActions[heldAction]!!
                heldAction.stopApplyingOn(this)
            }
        }
        // Undo default advances
        undoList.add(object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.gameTime -= time
                if (stoppedHolding != null) {
                    for (heldAction in stoppedHolding!!.keys) {
                        gameState.heldActions[heldAction] = stoppedHolding!![heldAction]!!
                    }
                }
            }
        })
        return UndoFactory.multiUndo(undoList)
    }

    fun gridLocation(x: Double, y: Double): Vector2Int {
        return Vector2Int((x / BlockWidth - gridX).toInt(), (y / BlockHeight).toInt())
    }
    fun gridLocation(point: Vector2Double): Vector2Int {
        return gridLocation(point.x, point.y)
    }
    fun gridLocationsBetween(a: Vector2Double, b: Vector2Double): ArrayList<Vector2Int> {
        return gridLocationsBetween(a.x, a.y, b.x, b.y);
    }
    fun gridLocationsBetween(ax: Double, ay: Double, bx: Double, by: Double): ArrayList<Vector2Int> {
        // assume a is lefter than b (has less Y)
        if (bx < ax) {
            return gridLocationsBetween(bx, by, ax, ay).reverse()
        }
        val epsilon = 0.00000

        fun autoRange(a: Int, b: Int): IntProgression {
            return if (a <= b) a..b else a downTo b
        }

        val aGrid = gridLocation(ax, ay)
        val bGrid = gridLocation(bx - epsilon, by + (if (by - ay > 0) epsilon else -epsilon))
        val res = ArrayList<Vector2Int>(bGrid.x - aGrid.x + Math.abs(bGrid.y - aGrid.y))
        val dirY = (by - ay) / (bx - ax)

        var lastGridY = aGrid.y
        // we'll go by vertical
        for (gridX in aGrid.x .. bGrid.x - 1) {
            val borderX = (gridX + 1) * BlockWidth
            val contactY = ay + (borderX - ax) * dirY
            val curGridY = (contactY / BlockHeight).toInt()
            for (gridY in autoRange(lastGridY, curGridY)) {
                res.add(Vector2Int(gridX, gridY))
            }
            lastGridY = curGridY
        }
        for (gridY in autoRange(lastGridY, bGrid.y)) {
            res.add(Vector2Int(bGrid.x, gridY))
        }
        return res
    }

    fun atLocation(x: Double, y:Double): GameObject? {
        return grid[gridLocation(x, y)]
    }

    fun textDump(): ArrayList<String> {
        // Convert objects to char grid
        val charGrid = Grid2D(grid.width, grid.height, { ' ' })
        for (gameObject in gameObjects) {
            val gridLoc = gridLocation(gameObject.x, gameObject.y)
            for (x in gridLoc.x .. gridLoc.x + gameObject.widthBlocks - 1) {
                for (y in gridLoc.y .. gridLoc.y + gameObject.heightBlocks - 1) {
                    charGrid[x, y] = gameObject.dumpChar
                }
            }
        }

        // Convert char grid to strings
        val strings = ArrayList<String>()
        for (y in (grid.height - 1).downTo(0)) {
            val sb = StringBuilder(grid.width)
            for (x in 0.. grid.width - 1) {
                sb.append(charGrid[x, y])
            }
            strings.add(sb.toString())
        }
        return strings
    }
    fun print() {
        for (line in textDump()) {
            println(line)
        }
    }

    fun getPerformableActions(): ArrayList<GameAction> {
        val performableActions = ArrayList<GameAction>()
        for (action in game.gameDescription.allActions) {
            if (action is HoldAction) {
                if (action.isApplicableOn(this))
                    performableActions.add(action.asStartAction)
                else if (action.canBeStoppedApplyingOn(this))
                    performableActions.add(action.asStopAction)
            } else if (action.isApplicableOn(this)) {
                performableActions.add(action)
            }
        }

        return performableActions
    }

    /**
     * This makes a deep copy of the state, effectively creating a new state that does not depend on the original.
     */
    fun makeCopy(): GameState {
        val stateCopy = GameState(game, levelGenerator)
        stateCopy.gridX = gridX
        stateCopy.heldActions = HashMap(heldActions)

        // Handle gameObjects
        stateCopy.gameObjects.clear()
        stateCopy.updateObjects.clear()
        stateCopy.grid.clear()
        for (gameObject in gameObjects) {
            val objectCopy = gameObject.makeCopy()
            objectCopy.gameState = stateCopy
            stateCopy.gameObjects.add(objectCopy)
            if (objectCopy.isUpdated)
                stateCopy.updateObjects.add(objectCopy)
            if (gameObject.gameObjectClass == GameObjectClass.PLAYER)
                stateCopy.player = objectCopy as Player
            else
                stateCopy.grid[gridLocation(objectCopy.location)] = objectCopy
        }

        stateCopy.isGameOver = isGameOver
        return stateCopy
    }

    override fun writeObject(oos: ObjectOutputStream): GameState {
        oos.writeInt(gridX)
        oos.writeInt(heldActions.count())
        for ((holdAction, time) in heldActions) {
            oos.writeObject(holdAction.javaClass.simpleName)
            oos.writeDouble(time)
        }
        oos.writeInt(gameObjects.count())
        for (gameObject in gameObjects) {
            oos.writeChar(gameObject.dumpChar.toInt())
            gameObject.writeObject(oos)
        }
        oos.writeBoolean(isGameOver)
        return this
    }

    override fun readObject(ois: ObjectInputStream): GameState {
        gridX = ois.readInt()

        heldActions.clear()
        val heldActionsCount = ois.readInt()
        for (i in 1 .. heldActionsCount) {
            val holdActionName = ois.readObject() as String
            for (holdAction in allActions) {
                if (holdAction.javaClass.simpleName == holdActionName) {
                    heldActions[holdAction as HoldAction] = ois.readDouble()
                    break
                }
            }
        }

        gameObjects.clear()
        updateObjects.clear()
        grid.clear()
        val gameObjectCount = ois.readInt()
        var foundPlayer = false
        for (i in 1 .. gameObjectCount) {
            val char: Char = ois.readChar()
            val gameObject = game.gameDescription.charToObject[char]!!.makeCopy()
            gameObject.readObject(ois)
            gameObject.gameState = this
            gameObjects.add(gameObject)
            if (gameObject.isUpdated)
                updateObjects.add(gameObject)
            if (gameObject.gameObjectClass == GameObjectClass.PLAYER) {
                if (foundPlayer)
                    throw Exception("Multiple players found while reading GameState")
                foundPlayer = true
                player = gameObject as Player
            } else {
                grid[gridLocation(gameObject.location)] = gameObject
            }
        }

        isGameOver = ois.readBoolean()
        return this
    }

    /**
     * Returns currently held actions as bit array (in an Int).
     * The bits are 1 - when actionIx is held and 0 when actionIx is not held, ordered from lowest to highest by the order
     * that the actions are returned in the allActions() method on GameState (also GameDescription).
     *
     * Useful for hashing.
     */
    fun currentHeldActionsAsFlags(): Int {
        var flags = 0
        var curFlag = 1
        for (action in allActions) {
            if (action is HoldAction) {
                if (heldActions.containsKey(action)) {
                    flags += curFlag
                }
                curFlag *= 2
            }
        }
        return flags
    }
}