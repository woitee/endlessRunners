package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.utils.arrayList
import java.util.*

class ColumnCopyingLevelGenerator(var savedColumn: List<GameObject?> = ArrayList()): LevelGenerator() {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val column = arrayList<GameObject?>(savedColumn.size, { null })
        for (i in column.indices) {
            column[i] = savedColumn[i]?.makeCopy()
        }
        return column
    }

    override fun init(gameState: GameState) {
    }
}