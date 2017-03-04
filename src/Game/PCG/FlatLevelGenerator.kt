package Game.PCG

import Game.GameObjects.GameObject
import Game.GameObjects.SolidBlock
import Game.GameState
import Game.HeightBlocks
import java.util.*
import Utils.arrayList

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
