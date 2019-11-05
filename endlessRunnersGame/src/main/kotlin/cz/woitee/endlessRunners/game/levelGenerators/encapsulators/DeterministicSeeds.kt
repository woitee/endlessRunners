package cz.woitee.endlessRunners.game.levelGenerators.encapsulators

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.objects.GameObject
import java.util.ArrayList

/**
 * A class that uses a deterministic progression of seeds which is advanced on a restart.
 * Thus, every run with this generator will produce the same sequence of levels.
 */
class DeterministicSeeds(val innerGenerator: LevelGenerator, startSeed: Long, val seedStep: Long = 6337) : LevelGenerator() {
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
