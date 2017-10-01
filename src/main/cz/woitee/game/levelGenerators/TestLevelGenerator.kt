package cz.woitee.game.levelGenerators

import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.GameState
import cz.woitee.game.HeightBlocks
import cz.woitee.game.objects.CustomBlock
import cz.woitee.utils.arrayList
import java.util.*

/**
 * Created by woitee on 23/07/2017.
 */

class TestLevelGenerator: LevelGenerator() {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        col[0] = SolidBlock()
        val gridX = gameState.gridX

        if (gridX % 20 == 0) {
            col[0] = CustomBlock(0)
        }

        if (gridX % 20 == 10) {
            col[1] = CustomBlock(1)
            col[2] = CustomBlock(1)
        }
        return col
    }

    override fun reset() {
    }
}