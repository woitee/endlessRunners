package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObject
import java.util.*

/**
 * A generator that only repeats the same column over and over.
 */
class ColumnCopyingLevelGenerator(var savedColumn: List<GameObject?> = ArrayList()) : LevelGenerator() {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        return copyColumn(savedColumn)
    }

    override fun init(gameState: GameState) {
    }
}
