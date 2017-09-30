package cz.woitee.game.levelGenerators

import cz.woitee.game.GameState
import cz.woitee.game.HeightBlocks
import cz.woitee.game.WidthBlocks
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.SolidBlock
import cz.woitee.utils.arrayList
import java.util.*

class OneScreenThenDeathEncapsulator(val innerGenerator: ILevelGenerator): ILevelGenerator {
    var numCalled = 0

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        if (numCalled <= WidthBlocks) {
            numCalled++
            return innerGenerator.generateNextColumn(gameState)
        }
        return arrayList(HeightBlocks, { SolidBlock() })
    }

    override fun reset() {
        innerGenerator.reset()
    }
}