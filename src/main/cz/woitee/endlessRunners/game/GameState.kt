package cz.woitee.endlessRunners.game

import java.util.*

import cz.woitee.endlessRunners.game.undoing.IUndo

import cz.woitee.endlessRunners.game.objects.Player
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.objects.UndoableUpdateGameObject
import cz.woitee.endlessRunners.game.effects.UndoableGameEffect
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.undoing.NoUndo
import cz.woitee.endlessRunners.game.undoing.UndoFactory
import cz.woitee.endlessRunners.geom.Vector2Double
import cz.woitee.endlessRunners.geom.Vector2Int
import cz.woitee.endlessRunners.utils.MySerializable
import cz.woitee.endlessRunners.utils.pools.DefaultUndoListPool
import cz.woitee.endlessRunners.utils.reverse
import nl.pvdberg.hashkode.hashKode
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * A state of the game, contains objects in the game, other statuses and provides methods to advance the game
 * and manipulate it's inner workings.
 *
 * Created by woitee on 13/01/2017.
 */

class GameState(val game: Game, val levelGenerator: LevelGenerator?, var tag: String = "") : MySerializable {
    var player = Player(0.0, 0.0)
    var gameObjects = arrayListOf<GameObject>(player)
    var updateObjects = arrayListOf<GameObject>(player)
    var grid = Grid2D<GameObject?>(WidthBlocks, HeightBlocks, { null })
    var gridX = 0
    var lastAdvanceTime = game.updateTime
        private set
    var gameTime = 0.0
        private set
    var buttons = ArrayList<GameButton>()
    var heldActions = HashMap<HoldButtonAction, Double>()
    var timedEffects = HashMap<Double, GameEffect>()

    var isGameOver = false

    val lastColumnX: Int
        get() = gridX + WidthBlocks
    val lastColumnXpx: Double
        get() = (lastColumnX * BlockWidth).toDouble()
    val allActions
        get() = game.gameDescription.allActions

    /** Maximum x of an object that is still in the state. */
    val maxX: Int
        get() = (gridX + grid.width) * BlockWidth

    /**
     * Version of serialization - if changed, can load GameStates saved with lower version (if object implement this)
     */
    public var serializationVersion = 5

    init {
        player.x = PlayerScreenX
        player.y = BlockHeight.toDouble()
        player.xspeed = game.gameDescription.playerStartingSpeed
        player.gameState = this
        for (i in 0 until WidthBlocks) {
            addToGrid(SolidBlock(), i, 0)
        }
        for (i in allActions.indices)
            buttons.add(GameButton(allActions[i], this, i))
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
    internal fun addColumn(column: ArrayList<GameObject?>): ArrayList<GameObject?> {
        return _addColumn(column)
    }
    internal fun undoAddColumn(column: ArrayList<GameObject?>): ArrayList<GameObject?> {
        return _addColumn(column, false)
    }
    /**
     * Adds a new column of GameObjects on selected side of the Grid (useful for undoing operations).
     */
    protected fun _addColumn(column: ArrayList<GameObject?>, toRight: Boolean = true): ArrayList<GameObject?> {
        val oldColumn = shiftGrid(toRight)
        val newColumn = column
        val newColumnX = if (toRight) WidthBlocks - 1 else 0
        for (y in 0..newColumn.lastIndex) {
            addToGrid(newColumn[y], newColumnX, y)
        }
        return oldColumn
    }

    /**
     * Advances the grid one square towards one of the direction, and returns the leftest column, that has fallen out of grid.
     */
    internal fun shiftGrid(towardsRight: Boolean = true): ArrayList<GameObject?> {
        val gridXDiff = if (towardsRight) 1 else -1
        val dropX = if (towardsRight) 0 else grid.width - 1

        gridX += gridXDiff
        val column = grid.getColumn(dropX)
        for (y in 0 .. grid.height - 1) {
            val gameObject = grid[dropX, y]
            gameObject ?: continue
            gameObjects.remove(gameObject)
            if (gameObject.isUpdated)
                updateObjects.remove(gameObject)
        }
        grid.shiftX(gridXDiff)
        return column
    }

    fun advance(time: Double) {
        lastAdvanceTime = time

        synchronized(gameObjects) {
            performActionsBasedOnButtons()

            val it = timedEffects.iterator()
            while (it.hasNext()) {
                val keyValue = it.next()
                if (gameTime >= keyValue.key) {
                    keyValue.value.applyOn(this)
                    it.remove()
                }
            }

            for ((targetTime, effect) in timedEffects) {
                if (gameTime >= targetTime) {
                    effect.applyOn(this)
                    timedEffects.remove(targetTime)
                }
            }
            for (gameObject in updateObjects) {
                gameObject.update(time)
            }
            for (gameEffect in game.gameDescription.permanentEffects) {
                gameEffect.applyOn(this)
            }
            gameTime += time
        }
    }

    fun advanceUndoable(time: Double): IUndo {
        synchronized(gameObjects) {
            val undoList = DefaultUndoListPool.borrowObject()
            performActionsBasedOnButtonsUndoably(undoList)

            val it = timedEffects.iterator()
            while (it.hasNext()) {
                val keyValue = it.next()
                val targetTime = keyValue.key
                val effect = keyValue.value
                if (gameTime >= targetTime) {
                    undoList.add((effect as UndoableGameEffect).applyUndoablyOn(this))
                    it.remove()
                    undoList.add(object : IUndo { override fun undo(gameState: GameState) {
                        timedEffects[targetTime] = effect
                    }})
                }
            }
            updateObjects.map {
                val undo = (it as UndoableUpdateGameObject).undoableUpdate(time)
                if (undo != NoUndo)
                    undoList.add(undo)
            }
            game.gameDescription.permanentEffects.map {
                val undo = (it as UndoableGameEffect).applyUndoablyOn(this)
                if (undo != NoUndo)
                    undoList.add(undo)
            }
            gameTime += time
            undoList.add(object : IUndo { override fun undo(gameState: GameState) {
                gameState.gameTime -= time
            }})
            return UndoFactory.multiUndo(undoList)
        }
    }

    fun performActionsBasedOnButtons() {
        for (button in buttons) {
            val action = button.action
            if (action !is HoldButtonAction) {
                // press actions whose buttons are held
                if (button.isPressed && action.isApplicableOn(this))
                    button.action.applyOn(this)
            } else {
                // button is HoldButtonAction
                if (button.isPressed && !action.isAppliedIn(this) && action.isApplicableOn(this)) {
                    action.applyOn(this)
                    heldActions[action] = gameTime
                } else if ((!button.isPressed || !action.canBeKeptApplyingOn(this)) && action.isAppliedIn(this) && action.canBeStoppedApplyingOn(this)) {
                    action.stopApplyingOn(this)
                    heldActions.remove(action)
                } else if (action.isAppliedIn(this)) {
                    action.keepApplyingOn(this)
                }
            }
        }
    }

    fun performActionsBasedOnButtonsUndoably(undoList: ArrayList<IUndo>) {
        for (button in buttons) {
            val action = button.action
            if (action !is HoldButtonAction) {
                // press actions whose buttons are held
                if (button.isPressed && action.isApplicableOn(this))
                    undoList.add(button.action.applyUndoablyOn(this))
            } else {
                // button is HoldButtonAction
                if (button.isPressed && !action.isAppliedIn(this) && action.isApplicableOn(this)) {
                    undoList.add(action.applyUndoablyOn(this))
                    heldActions[action] = gameTime
                    undoList.add(object : IUndo {
                        override fun undo(gameState: GameState) {
                            gameState.heldActions.remove(action)
                        }
                    })
                } else if ((!button.isPressed || !action.canBeKeptApplyingOn(this)) && action.isAppliedIn(this) && action.canBeStoppedApplyingOn(this)) {
                    undoList.add(action.stopApplyingUndoablyOn(this))
                    val originalTime = heldActions[action]!!
                    heldActions.remove(action)
                    undoList.add(object : IUndo {
                        override fun undo(gameState: GameState) {
                            gameState.heldActions[action] = originalTime
                        }
                    })
                } else if (action.isAppliedIn(this)) {
                    undoList.add(action.keepApplyingUndoablyOn(this))
                }
            }
        }
    }

    /**
     * Advances the gameState when using a specific action.
     */
    fun advanceByAction(action: GameButton.StateChange?, time: Double) {
        action?.applyOn(this)
        this.advance(time)
    }

    fun advanceUndoableByAction(action: GameButton.StateChange?, time: Double): IUndo {
        return UndoFactory.DoubleUndo(
            action?.applyUndoablyOn(this) ?: NoUndo,
            this.advanceUndoable(time)
        )
    }

    fun scroll(time: Double) {
        synchronized(gameObjects) {
            if (levelGenerator == null) {
                println("Level Generator should be set when scrolling!")
            } else {
                val offset = player.x - PlayerScreenX - (gridX * BlockWidth)
                val blockOffset = (offset / BlockWidth).toInt()

                for (i in 1..blockOffset) {
                    //                        println("GridChange $gridX #GameObjects ${gameObjects.size}")
                    val column = levelGenerator.generateNextColumn(this)
                    this.addColumn(column)
                }
            }
        }
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
        for (x in 0 until grid.width) {
            for (y in 0 until grid.height) {
                charGrid[x, y] = grid[x, y]?.dumpChar ?: ' '
            }
        }
        val gridLoc = gridLocation(player.x, player.y)
        charGrid[gridLoc.x, gridLoc.y] = 'P'
        charGrid[gridLoc.x, gridLoc.y + 1] = 'P'

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

    /**
     * Gets all the buttons the player can press / release.
     */
    fun getPerformableButtonInteractions(onlyHolds: Boolean = false): ArrayList<GameButton.StateChange> {
        val results = ArrayList<GameButton.StateChange>()
        for (button in buttons) {
            val action = button.action
            if (action !is HoldButtonAction && button.makesSenseToPress) {
                results.add(
                    GameButton.StateChange(button,
                        if (onlyHolds) GameButton.InteractionType.HOLD else GameButton.InteractionType.PRESS
                    )
                )
            } else if (action is HoldButtonAction && button.makesSenseToPress) {
                results.add(GameButton.StateChange(button,
                    GameButton.InteractionType.HOLD
                ))
            } else if (button.makesSenseToRelease) {
                results.add(GameButton.StateChange(button, GameButton.InteractionType.RELEASE))
            }
        }
        return results
    }

    /**
     * This makes a deep copy of the state, effectively creating a new state that does not depend on the original.
     */
    fun makeCopy(): GameState {
        val stateCopy = GameState(game, levelGenerator)
        stateCopy.gameTime = gameTime
        stateCopy.gridX = gridX
        for (i in 0 until buttons.size) {
            stateCopy.buttons[i].isPressed = buttons[i].isPressed
            stateCopy.buttons[i].pressedGameTime = buttons[i].pressedGameTime
        }

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

        stateCopy.lastAdvanceTime = lastAdvanceTime
        stateCopy.isGameOver = isGameOver
        return stateCopy
    }

    override fun writeObject(oos: ObjectOutputStream): GameState {
        oos.writeInt(serializationVersion)
        oos.writeDouble(gameTime)
        oos.writeInt(gridX)
//        oos.writeInt(pressedActions.count())
//        for ((pressedButton, time) in pressedActions) {
//            oos.writeObject(pressedButton.javaClass.simpleName)
//            oos.writeDouble(time)
//        }
        oos.writeInt(buttons.size)
        for (button in buttons) {
            oos.writeObject(button.action.javaClass.simpleName)
            oos.writeBoolean(button.isPressed)
            oos.writeDouble(button.pressedGameTime)
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
        // version 3 is when serialization version was added to retain backwards compatiblity
        // I know that backwards compatibility is good code killer, but since all this is used just for saving
        // gamestates for debugging and experiment resuming principles, it is not so bad

        if (serializationVersion >= 3)
            this.serializationVersion = ois.readInt()
        if (serializationVersion >= 4)
            this.gameTime = ois.readDouble()
        gridX = ois.readInt()

        if (serializationVersion < 5) {
            val pressedActionsCount = ois.readInt()
            for (i in 1 .. pressedActionsCount) {
                ois.readObject() as String
                ois.readDouble()
            }
        } else {
            val buttonCount = ois.readInt()
            if (buttonCount != buttons.size)
                throw Exception("Exception while serializing GameState: Wrong number of buttons! Is the same GameDescription used?")
            for (i in 0 until buttons.size) {
                val actionName = buttons[i].action.javaClass.simpleName
                val serializedName = ois.readObject() as String
                if (actionName != serializedName)
                    throw Exception("Exception while serializing GameState: Different buttons! Is the same GameDescription used?")
                buttons[i].isPressed = ois.readBoolean()
                buttons[i].pressedGameTime = ois.readDouble()
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
            gameObject.gameState = this
            gameObject.readObject(ois)
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

    override fun equals(other: Any?): Boolean {
        if (other !is GameState)
            return false

        if (gameObjects.size != other.gameObjects.size) return false
        if (gridX != other.gridX) return false
        if (gameTime != other.gameTime) return false
        if (isGameOver != other.isGameOver) return false

        if (buttons.size != other.buttons.size) return false

        for (i in 0 until buttons.size) {
            if (other.buttons[i] != buttons[i]) return false
            if (other.buttons[i].isPressed != buttons[i].isPressed) return false
            if (other.buttons[i].pressedGameTime != buttons[i].pressedGameTime) return false
        }

        if (player != other.player) return false
        for (gameObject in gameObjects) {
            if (gameObject.gameObjectClass != GameObjectClass.PLAYER) {
                val gridLocation = gridLocation(gameObject.location)
                val otherObject = other.grid[gridLocation] ?: return false
                if (gameObject.x != otherObject.x) return false
                if (gameObject.y != otherObject.y) return false
                if (gameObject.gameObjectClass != otherObject.gameObjectClass) return false
            }
        }
        return true
    }

    override fun hashCode() = hashKode(gameObjects.size, gridX, player.x, player.y, player.xspeed, player.yspeed, heldButtonsAsFlags())

    /**
     * Returns currently held actions as bit array (in an Int).
     * The bits are 1 - when actionIx is held and 0 when actionIx is not held, ordered from lowest to highest by the order
     * that the actions are returned in the allActions() method on GameState (also GameDescription).
     *
     * Useful for hashing.
     */
    fun heldButtonsAsFlags(): Int {
        var flags = 0
        var curFlag = 1
        for (button in buttons) {
            if (button.isPressed)
                flags += curFlag
            curFlag *= 2
        }
        return flags
    }
}