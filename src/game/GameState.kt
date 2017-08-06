package game

import java.util.*

import game.undoing.IUndo

import game.gameObjects.Player
import game.gameObjects.SolidBlock
import game.pcg.ILevelGenerator
import game.gameObjects.GameObject
import game.gameActions.abstract.GameAction
import game.gameActions.abstract.HoldAction
import game.gameObjects.UndoableUpdateGameObject
import game.gameEffects.UndoableGameEffect
import game.undoing.NoActionUndo
import game.undoing.UndoFactory
import geom.Vector2Double
import geom.Vector2Int
import utils.pools.DefaultUndoListPool
import utils.reverse

/**
 * Created by woitee on 13/01/2017.
 */

class GameState(val game: Game, val levelGenerator: ILevelGenerator?): Cloneable {
    var player = Player()
    var gameObjects = arrayListOf<GameObject>(player)
    var updateObjects = arrayListOf<GameObject>(player)
    var grid = Grid2D<GameObject?>(WidthBlocks, HeightBlocks, { null })
    var gridX = 0
    var lastAdvanceTime = 0.0
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
        if (gameObject != null) {
            gameObjects.add(gameObject)
            if (gameObject.isUpdated)
                updateObjects.add(gameObject)
            gameObject.x = ((gridX + x) * BlockWidth).toDouble()
            gameObject.y = (y * BlockHeight).toDouble()
            gameObject.gameState = this
        }
        grid[x, y] = gameObject
    }
    internal fun removeFromGrid(gameObject: GameObject?, x: Int, y:Int) {
        gameObject ?: return
        gameObjects.remove(gameObject)
        if (gameObject.isUpdated)
            updateObjects.remove(gameObject)
        grid[x, y] = null
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

        for (gameObject in updateObjects) {
            gameObject.update(time)
        }
        for (gameEffect in game.gameDescription.permanentEffects) {
            gameEffect.applyOn(this)
        }

        if (scrolling) {
            if (levelGenerator == null) {
                println("Level Generator should be set when scrolling!")
            } else {
                val offset = player.x - PlayerScreenX - (gridX * BlockWidth)
                val blockOffset = (offset / BlockWidth).toInt()

                synchronized(gameObjects) {
                    for (i in 1..blockOffset) {
                        shiftGrid()
//                        println("GridChange $gridX #GameObjects ${gameObjects.size}")
                        val column = levelGenerator.generateNextColumn(this)
                        for (y in 0..column.lastIndex) {
                            addToGrid(column[y], WidthBlocks - 1, y)
                        }
                    }
                }
            }
        }
        gameTime += time

        for (heldAction in heldActions.keys) {
            if (!heldAction.canBeKeptApplyingOn(this)) {
                heldAction.stopApplyingOn(this)
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
        return Vector2Int((x / BlockWidth).toInt(), (y / BlockHeight).toInt())
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
                res.add(Vector2Int(gridX - this.gridX, gridY))
            }
            lastGridY = curGridY
        }
        for (gridY in autoRange(lastGridY, bGrid.y)) {
            res.add(Vector2Int(bGrid.x - this.gridX, gridY))
        }
        return res
    }

    fun atLocation(x: Double, y:Double): GameObject? {
        val gridLoc = gridLocation(x, y)
        gridLoc.x -= gridX
        return grid[gridLoc]
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

    public override fun clone(): Any {
        return super.clone()
    }
}