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

    var probableTransfers = mapOf(
        -2 to 0.1,
        -1 to 0.15,
        0 to 0.45,
        +1 to 0.30,
        +2 to 0.00
    )

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = arrayList<GameObject?>(HeightBlocks, { null })
        for (i in 0 .. currentHeight - 1) {
            col[i] = SolidBlock()
        }
        var rnd = gameState.game.random.nextDouble()
        for ((key, value) in probableTransfers) {
            rnd -= value
            if (rnd < 0) {
                currentHeight += key
                break
            }
        }
        currentHeight = currentHeight.coerceIn(1, 6)
        return col
    }

    override fun reset() {
        currentHeight = 1
    }
}
