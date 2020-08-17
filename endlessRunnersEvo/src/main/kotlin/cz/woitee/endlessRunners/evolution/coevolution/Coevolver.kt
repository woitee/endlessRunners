package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.evoController.EvoControllerRunner
import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.evolution.evoGame.EvoGameRunner
import cz.woitee.endlessRunners.evolution.evoGame.EvolvedGameDescription
import cz.woitee.endlessRunners.evolution.utils.DateUtils
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.utils.addOrPut
import cz.woitee.endlessRunners.utils.except
import io.jenetics.DoubleGene
import io.jenetics.IntegerGene
import io.jenetics.engine.EvolutionResult
import kotlin.random.Random

class Coevolver (val seed: Long? = null) {
    val timestamp = DateUtils.timestampString()
    val random = if (seed == null) Random.Default else Random(seed)

    val blockPopulations = ArrayList<EvolutionResult<IntegerGene, Int>>()
    var controllerPopulation: EvolutionResult<DoubleGene, Double>? = null
    var gameDescriptionPopulation: EvolutionResult<DoubleGene, Double>? = null

    // Evolved blocks
    val evolvedBlocks = ArrayList<HeightBlock>()
    // Evolved blocks plus the default ones
    val currentBestBlocks = ArrayList<HeightBlock>()
    var currentBestController = EvolvedPlayerController(EvolvedPlayerController.sampleGenotype())
    var currentBestGameDescription = EvolvedGameDescription(EvolvedGameDescription.sampleGenotype())

    fun evolveBlocks(numGenerations: Long, numBlocks: Int, printStats: Boolean = false): List<HeightBlock> {
        val evoBlockRunner = EvoBlockRunner(
            currentBestGameDescription,
            { EvolvedPlayerController(currentBestController.genotype) },
            numGenerations,
            false,
            csvLoggingPrefix = "coevo_$timestamp/",
            seed = random.nextLong()
        )

        val firstIteration = blockPopulations.isEmpty()

        for (j in 0 until numBlocks) {
            val otherBlocks = evolvedBlocks.except(j)
            val blockResult = if (firstIteration) {
                evoBlockRunner.evolveToResult(otherBlocks, null, 0)
            } else {
                evoBlockRunner.evolveToResult(otherBlocks, blockPopulations[j].genotypes, blockPopulations[j].generation)
            }

            val block = evoBlockRunner.genotype2block(blockResult.bestPhenotype.genotype)
            evolvedBlocks.addOrPut(j, block)
            blockPopulations.addOrPut(j, blockResult)
        }

        currentBestBlocks.clear()
        currentBestBlocks.addAll(evoBlockRunner.defaultBlocks)
        currentBestBlocks.addAll(evolvedBlocks)

        if (printStats) {
            println("Avg block fitness: ${blockPopulations.map { it.bestFitness }.average()}")

            for ((j, block) in currentBestBlocks.withIndex()) {
                println("block $j attributes: ${evoBlockRunner.getFitnessValues(block, currentBestBlocks.except(j))}")
            }
        }

        return evolvedBlocks
    }
    fun evolveController(numGenerations: Long) {
        val evoControllerRunner = EvoControllerRunner(
                currentBestGameDescription,
                { HeightBlockLevelGenerator(currentBestGameDescription, currentBestBlocks) },
                csvLoggingPrefix = "coevo_$timestamp/",
                numGenerations = numGenerations,
                seed = random.nextLong()
        )

        controllerPopulation = evoControllerRunner.evolveToResult(
                controllerPopulation?.genotypes,
                controllerPopulation?.generation ?: 0
        )

        currentBestController = EvolvedPlayerController(controllerPopulation!!.bestPhenotype.genotype)
    }
    fun evolveDescription(numGenerations: Long) {
        val evoGameRunner = EvoGameRunner(
                { EvolvedPlayerController(currentBestController.genotype) },
                { EvolvedPlayerController(currentBestController.genotype) },
                currentBestBlocks,
                numGenerations = numGenerations,
                csvLoggingPrefix = "coevo_$timestamp/",
                seed = random.nextLong()
        )

        gameDescriptionPopulation = evoGameRunner.evolveToResult(gameDescriptionPopulation?.genotypes, gameDescriptionPopulation?.generation ?: 0)
        currentBestGameDescription = EvolvedGameDescription(gameDescriptionPopulation!!.bestPhenotype.genotype)
    }

    fun getBestTriple(): CoevolvedTriple {
        return CoevolvedTriple(currentBestBlocks, currentBestController, currentBestGameDescription)
    }
}