package cz.woitee.endlessRunners.game.levelGenerators

import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.objects.CustomBlock
import java.util.*
import cz.woitee.endlessRunners.utils.arrayList

/**
 * Created by woitee on 04/03/2017.
 */

class SimpleLevelGenerator: LevelGenerator() {
    var currentHeight = 1

    var probableTransfers = mapOf(
        -2 to 0.10,
        -1 to 0.13,
        0 to 0.57,
        +1 to 0.23,
        +2 to 0.00
    )
//    var probableTransfers = mapOf(
//            -2 to 0.10,
//            -1 to 0.10,
//            0 to 0.70,
//            +1 to 0.10,
//            +2 to 0.00
//    )

    var tunnelProbability = 0.02

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val col = generateEmptyColumn()
        for (i in 0 until currentHeight) {
            col[i] = SolidBlock()
        }
        val random = gameState.game.random
        if (random.nextDouble() < tunnelProbability) {
            col[currentHeight + 1] = SolidBlock()
        } else {
            // Blocked Tunnels
//            if (random.nextDouble() < 0.01) {
//                col[currentHeight] = CustomBlock(1)
//            } else if (random.nextDouble() < 0.03) {
//                col[currentHeight + 2] = CustomBlock(1)
//            }
//        } else {
//            // Trampoline
//            if (random.nextDouble() < 0.02) {
//                col[currentHeight - 1] = CustomBlock(0)
//            }
//            // Blockage of various heights
//            if (random.nextDouble() < 0.03) {
//                col[currentHeight] = CustomBlock(1)
//                if (random.nextDouble() < 0.5) {
//                    col[currentHeight + 1] = CustomBlock(1)
//                    if (random.nextDouble() < 0.5) {
//                        col[currentHeight + 2] = CustomBlock(1)
//                        if (random.nextDouble() < 0.5) {
//                            col[currentHeight + 3] = CustomBlock(1)
//                        }
//                    }
//                }
//            }
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

    override fun init(gameState: GameState) {
        currentHeight = 1
    }
}
