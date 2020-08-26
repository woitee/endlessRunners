package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.EvoProgressAccumulator
import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.evoController.EvoControllerRunner
import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.evolution.evoGame.EvoGameRunner
import cz.woitee.endlessRunners.evolution.evoGame.EvolvedGameDescription
import cz.woitee.endlessRunners.evolution.utils.DateUtils
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.utils.MySerializable
import cz.woitee.endlessRunners.utils.SerializableRandom
import cz.woitee.endlessRunners.utils.addOrPut
import cz.woitee.endlessRunners.utils.except
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.IntegerGene
import io.jenetics.engine.EvolutionResult
import io.jenetics.prngine.LCG64ShiftRandom
import io.jenetics.util.RandomRegistry
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.random.Random

class Coevolver(val seed: Long = Random.Default.nextLong()) : MySerializable {
    val timestamp = DateUtils.timestampString()
    private var random = SerializableRandom(seed)

    val blockPopulations = ArrayList<EvolutionResult<IntegerGene, Int>>()
    var controllerPopulation: EvolutionResult<DoubleGene, Double>? = null
    var gameDescriptionPopulation: EvolutionResult<DoubleGene, Double>? = null

    // Evolved blocks
    val evolvedBlocks = ArrayList<HeightBlock>()
    // Evolved blocks plus the default ones
    val currentBestBlocks = ArrayList<HeightBlock>()
    var currentBestController: EvolvedPlayerController
    var currentBestGameDescription: EvolvedGameDescription

    val evoProgressAccumulator = EvoProgressAccumulator()

    init {
        RandomRegistry.setRandom(LCG64ShiftRandom.ThreadSafe(seed))
        currentBestController = EvolvedPlayerController(EvolvedPlayerController.sampleGenotype())
        currentBestGameDescription = EvolvedGameDescription(EvolvedGameDescription.sampleGenotype())
    }

    fun evolveBlocks(numGenerations: Long, populationSize: Int, numBlocks: Int, printStats: Boolean = false): List<HeightBlock> {
        val evoBlockRunner = EvoBlockRunner(
            currentBestGameDescription,
            { EvolvedPlayerController(currentBestController.genotype) },
            numGenerations,
            populationSize,
            false,
            csvLoggingPrefix = "coevo_$timestamp/",
            seed = random.nextLong(),
            evoProgressAccumulator = evoProgressAccumulator
        )

        val firstIteration = blockPopulations.isEmpty()

        for (j in 0 until numBlocks) {
            evoBlockRunner.accumulatorKey = "-$j"
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
    fun evolveController(numGenerations: Long, populationSize: Int) {
        val evoControllerRunner = EvoControllerRunner(
                currentBestGameDescription,
                { HeightBlockLevelGenerator(currentBestGameDescription, currentBestBlocks) },
                csvLoggingPrefix = "coevo_$timestamp/",
                numGenerations = numGenerations,
                populationSize = populationSize,
                seed = random.nextLong(),
                evoProgressAccumulator = evoProgressAccumulator
        )

        controllerPopulation = evoControllerRunner.evolveToResult(
                controllerPopulation?.genotypes,
                controllerPopulation?.generation ?: 0
        )

        currentBestController = EvolvedPlayerController(controllerPopulation!!.bestPhenotype.genotype)
    }
    fun evolveDescription(numGenerations: Long, populationSize: Int): EvoGameRunner.FitnessWithReasons {
        val evoGameRunner = EvoGameRunner(
                { EvolvedPlayerController(currentBestController.genotype) },
                { EvolvedPlayerController(currentBestController.genotype) },
                currentBestBlocks,
                numGenerations = numGenerations,
                populationSize = populationSize,
                csvLoggingPrefix = "coevo_$timestamp/",
                seed = random.nextLong(),
                evoProgressAccumulator = evoProgressAccumulator
        )

        gameDescriptionPopulation = evoGameRunner.evolveToResult(gameDescriptionPopulation?.genotypes, gameDescriptionPopulation?.generation ?: 0)
        currentBestGameDescription = EvolvedGameDescription(gameDescriptionPopulation!!.bestPhenotype.genotype)

        return evoGameRunner.fitnessWithReasoning(currentBestGameDescription)
    }

    fun currentBestTriple(): CoevolvedTriple {
        return CoevolvedTriple(currentBestBlocks, currentBestController, currentBestGameDescription)
    }

    override fun writeObject(oos: ObjectOutputStream): Coevolver {
        oos.writeObject(random)

        oos.writeInt(blockPopulations.size)
        for (populations in blockPopulations) {
            oos.writeObject(populations)
        }
        oos.writeObject(controllerPopulation)
        oos.writeObject(gameDescriptionPopulation)

        for (block in evolvedBlocks) {
            oos.writeObject(block)
        }
        oos.writeObject(currentBestController.genotype)
        oos.writeObject(currentBestGameDescription.genotype)

        return this
    }
    override fun readObject(ois: ObjectInputStream): Coevolver {
        random = ois.readObject() as SerializableRandom

        val numBlocks = ois.readInt()

        blockPopulations.clear()
        repeat(numBlocks) {
            blockPopulations.add(ois.readObject() as EvolutionResult<IntegerGene, Int>)
        }
        controllerPopulation = ois.readObject() as EvolutionResult<DoubleGene, Double>?
        gameDescriptionPopulation = ois.readObject() as EvolutionResult<DoubleGene, Double>

        evolvedBlocks.clear()
        repeat(numBlocks) {
            evolvedBlocks.add(ois.readObject() as HeightBlock)
        }

        currentBestController = EvolvedPlayerController(ois.readObject() as Genotype<DoubleGene>)
        currentBestGameDescription = EvolvedGameDescription(ois.readObject() as Genotype<DoubleGene>)

        currentBestBlocks.clear()
        currentBestBlocks.addAll(
                EvoBlockRunner(currentBestGameDescription, { EvolvedPlayerController(currentBestController.genotype) }).defaultBlocks
        )
        currentBestBlocks.addAll(evolvedBlocks)

        return this
    }
}
