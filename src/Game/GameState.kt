package Game

import java.util.*

import Game.GameObjects.Player
import Game.GameObjects.SolidBlock
import Game.PCG.ILevelGenerator
import Game.GameObjects.GameObject
import Game.Settings.*

/**
 * Created by woitee on 13/01/2017.
 */

open class GameState(val levelGenerator: ILevelGenerator?) {
    var player = Player()
    var gameObjects = arrayListOf<GameObject>(player)
    var grid = Grid2D<GameObject?>(WidthBlocks, HeightBlocks, { null })
    var gridX = 0

    val lastColumnX: Int
        get() = gridX + WidthBlocks
    val lastColumnXpx: Double
        get() = (lastColumnX * BlockWidth).toDouble()

    init {
        player.x = PlayerScreenX
        player.y = BlockHeight.toDouble()
        player.xspeed = PlayerStartingSpeed
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
}