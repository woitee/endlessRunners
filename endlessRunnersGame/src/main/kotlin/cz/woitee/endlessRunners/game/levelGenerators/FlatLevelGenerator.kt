package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.utils.arrayList
import java.util.*

/**
 * Generator for generating only the basic flat level.
 */

class FlatLevelGenerator : LevelGenerator() {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        col[0] = SolidBlock()
        return col
    }

    override fun init(gameState: GameState) {
    }
}
