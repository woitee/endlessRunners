package cz.woitee.game.levelGenerators.encapsulators

import cz.woitee.game.GameState
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.game.objects.GameObject
import java.util.ArrayList

class DeterministicSeeds(val innerGenerator: LevelGenerator, startSeed: Long, val seedStep: Long = 6337): LevelGenerator() {
    var currentSeed = startSeed

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        return innerGenerator.generateNextColumn(gameState)
    }

    override fun init(gameState: GameState) {
        gameState.game.random.setSeed(currentSeed)
        currentSeed += seedStep
        innerGenerator.init(gameState)
    }
}