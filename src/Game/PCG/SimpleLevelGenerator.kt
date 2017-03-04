package Game.PCG

import Game.GameObjects.GameObject
import Game.GameObjects.SolidBlock
import Game.GameState
import Game.HeightBlocks
import java.util.*
import Utils.arrayList

/**
 * Created by woitee on 04/03/2017.
 */

class SimpleLevelGenerator: ILevelGenerator {
    var currentHeight = 1

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        for (i in 0 .. currentHeight - 1) {
            col[i] = SolidBlock()
        }
        if (gameState.gridX % 10 == 0) {
            currentHeight = if (currentHeight == 1) 2 else 1
        }
        return col
    }
}
