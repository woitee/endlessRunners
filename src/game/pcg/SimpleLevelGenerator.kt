package game.pcg

import game.gameObjects.GameObject
import game.gameObjects.SolidBlock
import game.GameState
import game.HeightBlocks
import java.util.*
import utils.arrayList

/**
 * Created by woitee on 04/03/2017.
 */

class SimpleLevelGenerator: ILevelGenerator {
    var currentHeight = 1

    var probableTransfers = mapOf(
        -2 to 0.10,
        -1 to 0.14,
        0 to 0.54,
        +1 to 0.22,
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
        currentHeight = currentHeight.coerceIn(1, 8)
        return col
    }

    override fun reset() {
        currentHeight = 1
    }
}
