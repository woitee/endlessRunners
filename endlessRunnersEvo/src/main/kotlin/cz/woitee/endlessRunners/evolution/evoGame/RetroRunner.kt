package cz.woitee.endlessRunners.evolution.evoGame

import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.utils.JavaSerializationUtils
import io.jenetics.*
import io.jenetics.engine.EvolutionResult

/**
 * A simple main class to rerun a result of the evolution.
 */
class RetroRunner() {
    fun runGenotypeFromString(string: String) {
        val population = JavaSerializationUtils.unserializeFromString<EvolutionResult<DoubleGene, Double>>(string)!!
        val genotype = population.bestPhenotype.genotype
        val gameDescription = EvolvedGameDescription(genotype)

        val evoBlockRunner = EvoBlockRunner(gameDescription, { DFSPlayerController() })
        val evolvedBlocks = evoBlockRunner.evolveMultipleBlocks(10)

        println("Retro-running game, which had fitness: ${population.bestPhenotype.fitness}")
        val controller = DFSPlayerController(DelayedTwinDFS(0.1))
        evoBlockRunner.runGameWithBlocks(evolvedBlocks, controller)
    }
}
