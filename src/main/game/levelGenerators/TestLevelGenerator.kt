package game.levelGenerators

import game.objects.GameObject
import game.objects.SolidBlock
import game.GameState
import game.HeightBlocks
import game.objects.CustomBlock
import utils.arrayList
import java.util.*

/**
 * Created by woitee on 23/07/2017.
 */

class TestLevelGenerator: ILevelGenerator {
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