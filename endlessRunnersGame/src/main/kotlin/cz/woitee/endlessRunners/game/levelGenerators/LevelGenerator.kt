package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.utils.arrayList
import java.util.*

/**
 * A LevelGenerator component of the game, this provides objects for the game column by column, thus generating the level.
 */

abstract class LevelGenerator {
    /**
     * Useful utility classes.
     */
    companion object {
        fun copyColumn(column: List<GameObject?>): ArrayList<GameObject?> {
            val copy = arrayList<GameObject?>(column.size) { null }
            for (i in column.indices) {
                copy[i] = column[i]?.makeCopy()
            }
            return copy
        }

        fun generateEmptyColumn(): ArrayList<GameObject?> {
            return arrayList(HeightBlocks) { null }
        }
    }

    /**
     * Generate new column for a given GameState.
     */
    abstract fun generateNextColumn(gameState: GameState): ArrayList<GameObject?>

    /**
     * Init values in the LevelGenerator. Called at the beginning and after each GameOver.
     */
    abstract fun init(gameState: GameState)
    /**
     * Level Generators get notified about game updates - they may or may not use this information.
     */
    open fun onUpdate(updateTime: Double, appliedAction: GameButton.StateChange?, gameState: GameState) {}
}
