package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.utils.arrayList
import java.util.*

/**
 * Created by woitee on 23/07/2017.
 */

class TestLevelGenerator: LevelGenerator() {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        col[0] = SolidBlock()
        val gridX = gameState.gridX

        if (gridX % 20 == 0 || gridX % 20 == 1) {
        } else {
            col[1] = SolidBlock()
        }

        return col
    }

    override fun init(gameState: GameState) {
    }
}