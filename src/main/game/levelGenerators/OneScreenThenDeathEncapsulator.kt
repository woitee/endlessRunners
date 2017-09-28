package game.levelGenerators

import game.GameState
import game.HeightBlocks
import game.WidthBlocks
import game.objects.GameObject
import game.objects.SolidBlock
import utils.arrayList
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