package game.levelGenerators

import game.objects.GameObject
import game.objects.SolidBlock
import game.GameState
import game.HeightBlocks
import java.util.*
import utils.arrayList

/**
 * Created by woitee on 15/01/2017.
 */

class FlatLevelGenerator: ILevelGenerator {
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        col[0] = SolidBlock()
        return col
    }

    override fun reset() {
    }
}
