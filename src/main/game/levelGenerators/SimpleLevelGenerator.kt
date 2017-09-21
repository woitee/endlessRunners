package game.levelGenerators

import game.gameObjects.GameObject
import game.gameObjects.SolidBlock
import game.GameState
import game.HeightBlocks
import game.gameObjects.CustomBlock
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
        val random = gameState.game.random
        if (random.nextDouble() < 0.1) {
            col[currentHeight + 1] = SolidBlock()
            if (random.nextDouble() < 0.01) {
                col[currentHeight] = CustomBlock(1)
            } else if (random.nextDouble() < 0.03) {
                col[currentHeight + 2] = CustomBlock(1)
            }
        } else {
            if (random.nextDouble() < 0.02) {
                col[currentHeight - 1] = CustomBlock(0)
            }
            if (random.nextDouble() < 0.03) {
                col[currentHeight] = CustomBlock(1)
                if (random.nextDouble() < 0.5) {
                    col[currentHeight + 1] = CustomBlock(1)
                    if (random.nextDouble() < 0.5) {
                        col[currentHeight + 2] = CustomBlock(1)
                        if (random.nextDouble() < 0.5) {
                            col[currentHeight + 3] = CustomBlock(1)
                        }
                    }
                }
            }
            var rnd = random.nextDouble()
            for ((key, value) in probableTransfers) {
                rnd -= value
                if (rnd < 0) {
                    currentHeight += key
                    break
                }
            }
        }
        currentHeight = currentHeight.coerceIn(1, 8)
        return col
    }

    override fun reset() {
        currentHeight = 1
    }
}
