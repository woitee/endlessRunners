package Game

import java.util.*

import Game.GameObjects.Player
import Game.GameObjects.SolidBlock
import Game.PCG.ILevelGenerator
import Game.GameObjects.GameObject
import Game.GameActions.IGameAction
import Game.Settings.*
import Geom.PointDouble
import Geom.PointInt
import Utils.reverse
import java.awt.geom.Point2D

/**
 * Created by woitee on 13/01/2017.
 */

open class GameState(val game: Game, val levelGenerator: ILevelGenerator?) {
    var player = Player()
    var gameObjects = arrayListOf<GameObject>(player)
    var grid = Grid2D<GameObject?>(WidthBlocks, HeightBlocks, { null })
    var gridX = 0
    val gameDescription = GameDescription()

    val lastColumnX: Int
        get() = gridX + WidthBlocks
    val lastColumnXpx: Double
        get() = (lastColumnX * BlockWidth).toDouble()

    init {
        player.x = PlayerScreenX
        player.y = BlockHeight.toDouble()
        player.xspeed = game.gameInfo.playerStartingSpeed
        for (i in 0 .. WidthBlocks - 1) {
            addToGrid(SolidBlock(), i, 0)
        }
    }

    internal fun addToGrid(gameObject: GameObject?, x: Int, y:Int) {
        if (gameObject != null) {
            gameObjects.add(gameObject)
            gameObject.x = ((gridX + x) * BlockWidth).toDouble()
            gameObject.y = (y * BlockHeight).toDouble()
        }
        grid[x, y] = gameObject
    }
    private fun removeFromGrid(gameObject: GameObject?, x: Int, y:Int) {
        gameObjects.remove(gameObject)
        grid[x, y] = null
    }
    private fun shiftGrid() {
        gridX += 1
        for (y in 0 .. grid.height - 1) {
            gameObjects.remove(grid[0, y])
        }
        grid.shiftX(1)
        System.gc()
    }

    fun advance(time: Long, scrolling:Boolean = false) {
        for (gameObject in gameObjects) {
            if (gameObject.isUpdated)
                gameObject.update(time)
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
                        println("GridChange $gridX #GameObjects ${gameObjects.size}")
                        val column = levelGenerator.generateNextColumn(this)
                        for (y in 0..column.lastIndex) {
                            addToGrid(column[y], WidthBlocks - 1, y)
                        }
                    }
                }
            }
        }
    }

    fun gridLocation(x: Double, y: Double): PointInt {
        return PointInt((x / BlockWidth).toInt(), (y / BlockHeight).toInt())
    }

    fun gridLocationsBetween(ax: Double, ay: Double, bx: Double, by: Double): ArrayList<PointInt> {
        // assume a is lefter than b (has less Y)
        if (bx < ax) {
            return gridLocationsBetween(bx, by, ax, ay).reverse()
        }
        val epsilon = 0.000001

        fun autoRange(a: Int, b: Int): IntProgression {
            return if (a <= b) a..b else a downTo b
        }

        val aGrid = gridLocation(ax, ay)
        val bGrid = gridLocation(bx - epsilon, by - epsilon)
        val res = ArrayList<PointInt>(bGrid.x - aGrid.x + Math.abs(bGrid.y - aGrid.y))
        val dir = PointDouble(1.0, (by - ay) / (bx - ax))

        var lastGridY = aGrid.y
        // we'll go by vertical
        for (gridX in aGrid.x .. bGrid.x - 1) {
            val borderX = (gridX + 1) * BlockWidth
            val contactY = ax + (borderX - ax) * dir.y
            val curGridY = (contactY / BlockHeight).toInt()
            for (gridY in autoRange(lastGridY, curGridY)) {
                res.add(PointInt(gridX, gridY))
            }
            lastGridY = curGridY
        }
        for (gridY in autoRange(lastGridY, bGrid.y)) {
            res.add(PointInt(bGrid.x, gridY))
        }
        return res
    }

    fun getPerformableActions(): List<IGameAction> {
        return game.gameInfo.allActions.filter { it -> it.isPerformableOn(this) }
    }
}