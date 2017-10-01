package cz.woitee.game.levelGenerators

import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.GameState
import cz.woitee.game.HeightBlocks
import java.util.*
import cz.woitee.utils.arrayList

/**
 * Created by woitee on 15/01/2017.
 */

class FlatLevelGenerator: LevelGenerator() {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        col[0] = SolidBlock()
        return col
    }

    override fun reset() {
    }
}
