package cz.woitee.endlessRunners.game.levelGenerators.encapsulators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.WidthBlocks
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.utils.arrayList

class OneScreenThenDeath(val innerGenerator: LevelGenerator) : LevelGenerator() {
    var numCalled = 0

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        if (numCalled <= WidthBlocks) {
            numCalled++
            return innerGenerator.generateNextColumn(gameState)
        }
        return arrayList(HeightBlocks, { SolidBlock() })
    }

    override fun init(gameState: GameState) {
        innerGenerator.init(gameState)
    }
}
