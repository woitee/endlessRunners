package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.SolidBlock
import java.util.*

/**
 * The specific method of generating level used in the original Canabalt game. It creates platforms with random
 * heights and lenghts and random gaps between them. The gaps are longer the faster the player runs.
 *
 * Slow-down obstacles are placed randomly.
 */
class CanabalLevelGenerator : LevelGenerator() {
    var isInGap = false
    var currentBuildingHeight = 1
    var currentSectionOffset = 0
    var currentSectionLength = 2 // This generates two more columns after starting screen and then starts
    var currentSectionColumn = ArrayList<GameObject?>()
    var nextObstacleOffset = -1

    val emptyColumn = generateEmptyColumn()
    lateinit var random: Random

    override fun init(gameState: GameState) {
        isInGap = false
        currentBuildingHeight = 1
        currentSectionOffset = 0
        currentSectionLength = 2
        currentSectionColumn = gameState.grid.getColumn(gameState.grid.width - 1)
        nextObstacleOffset = -1
        random = gameState.game.random
    }

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        ++currentSectionOffset
        if (currentSectionOffset >= currentSectionLength) startNewSection(gameState)

        val column = copyColumn(currentSectionColumn)
        if (currentSectionOffset == nextObstacleOffset) {
            nextObstacleOffset = -1
            addObstacle(column)
        }
        return column
    }

    fun startNewSection(gameState: GameState) {
        currentSectionOffset = 0
        if (isInGap) {
            currentBuildingHeight += random.nextInt(3) - 1
            currentBuildingHeight = currentBuildingHeight.coerceIn(1 .. 5)

            val column = generateEmptyColumn()
            for (i in 0 until currentBuildingHeight) {
                column[i] = SolidBlock()
            }
            isInGap = false
            currentSectionLength = 12 + (random.nextInt(9) - 4)
            currentSectionColumn = column
            if (gameState.player.xspeed > 15 && random.nextDouble() < 0.5) {
                nextObstacleOffset = currentSectionLength / 2 + (random.nextInt(3) - 1)
            }
        } else {
            isInGap = true
            currentSectionLength = (gameState.player.xspeed.toInt()) / 3 + (random.nextInt(5) - 2)
            currentSectionColumn = emptyColumn
        }
    }

    fun addObstacle(column: ArrayList<GameObject?>) {
        for (i in column.indices) {
            if (column[i] == null) {
                column[i] = CustomBlock(0)
                break
            }
        }
    }
}
