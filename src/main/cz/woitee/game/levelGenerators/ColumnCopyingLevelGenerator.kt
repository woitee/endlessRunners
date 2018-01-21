package cz.woitee.game.levelGenerators

import cz.woitee.game.GameState
import cz.woitee.game.objects.GameObject
import cz.woitee.utils.arrayList
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