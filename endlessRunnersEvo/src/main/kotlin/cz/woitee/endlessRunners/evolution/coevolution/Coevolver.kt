package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.EvoProgressAccumulator
import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.evoController.EvoControllerRunner
import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.evolution.evoGame.EvoGameRunner
import cz.woitee.endlessRunners.evolution.evoGame.EvolvedGameDescription
import cz.woitee.endlessRunners.evolution.utils.DateUtils
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.utils.*
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.IntegerGene
import io.jenetics.engine.EvolutionResult
import io.jenetics.prngine.LCG64ShiftRandom
import io.jenetics.util.RandomRegistry
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.random.Random

typealias IntPopulation = Iterable<Genotype<IntegerGene>>
typealias DoublePopulation = Iterable<Genotype<DoubleGene>>

class Coevolver(
    val numBlocks: Int,
    val blockPopulationSize: Int,
    val controllerPopulationSize: Int,
    val gameDescriptionPopulationSize: Int,
    val seed: Long = Random.Default.nextLong(),
    val headless: Boolean = false,
) : MySerializable {

    val timestamp = DateUtils.timestampString()
    private var random = SerializableRandom(seed)

    val blockEvoStates = arrayList<EvolutionResult<IntegerGene, Int>?>(numBlocks) { null }
    var controllerEvoState: EvolutionResult<DoubleGene, Double>? = null
    var gameDescriptionEvoState: EvolutionResult<DoubleGene, Double>? = null

    // Populations set to be used next time
    val nextBlockPopulations: ArrayList<IntPopulation?> = arrayList(numBlocks) { null }
    var nextControllerPopulation: DoublePopulation? = null
    var nextGameDescriptionPopulation: DoublePopulation? = null

    // Evolved blocks
    val evolvedBlocks = ArrayList<HeightBlock>()
    // Evolved blocks plus the default ones
    val currentBestBlocks = ArrayList<HeightBlock>()
    var currentBestController: EvolvedPlayerController
    var currentBestGameDescription: EvolvedGameDescription

    private val evoProgressAccumulator = if (headless) null else EvoProgressAccumulator()

    init {
        RandomRegistry.setRandom(LCG64ShiftRandom.ThreadSafe(seed))
        currentBestController = EvolvedPlayerController(EvolvedPlayerController.sampleGenotype())
        currentBestGameDescription = EvolvedGameDescription(EvolvedGameDescription.sampleGenotype())
    }

    fun evolveBlocks(numGenerations: Long, printStats: Boolean = false): List<HeightBlock> {
        val evoBlockRunner = EvoBlockRunner(
            currentBestGameDescription,
            { EvolvedPlayerController(currentBestController.genotype) },
            numGenerations,
            blockPopulationSize,
            false,
            csvLoggingPrefix = "coevo_$timestamp/",
            seed = random.nextLong(),
            evoProgressAccumulator = evoProgressAccumulator
        )

        for (j in 0 until numBlocks) {
            evoBlockRunner.accumulatorKey = "-$j"
            val otherBlocks = evolvedBlocks.take(j)
            val blockResult = evoBlockRunner.evolveToResult(
                otherBlocks,
                nextBlockPopulations[j],
                blockEvoStates[j]?.generation ?: 1
            )

            val block = evoBlockRunner.genotype2block(blockResult.bestPhenotype.genotype)
            evolvedBlocks.addOrPut(j, block)
            blockEvoStates.addOrPut(j, blockResult)
            nextBlockPopulations.addOrPut(j, blockResult.genotypes)
        }
        while (evolvedBlocks.size > numBlocks) {
            evolvedBlocks.pop()
        }

        currentBestBlocks.clear()
        currentBestBlocks.addAll(evoBlockRunner.defaultBlocks)
        currentBestBlocks.addAll(evolvedBlocks)

        if (printStats) {
            println("Avg block fitness: ${blockEvoStates.map { it!!.bestFitness }.average()}")

            for ((j, block) in currentBestBlocks.withIndex()) {
                evoBlockRunner.existingBlocks = evolvedBlocks.take(j)
                val fitnessValues = evoBlockRunner.getFitnessValues(block)
                println("block $j attributes: $fitnessValues")
                evoBlockRunner.getFitnessValues(block)
            }
        }

        return evolvedBlocks
    }
    fun evolveController(numGenerations: Long): EvolvedPlayerController {
        val evoControllerRunner = EvoControllerRunner(
            currentBestGameDescription,
            { HeightBlockLevelGenerator(currentBestGameDescription, currentBestBlocks) },
            csvLoggingPrefix = "coevo_$timestamp/",
            numGenerations = numGenerations,
            populationSize = controllerPopulationSize,
            seed = random.nextLong(),
            evoProgressAccumulator = evoProgressAccumulator
        )

        controllerEvoState = evoControllerRunner.evolveToResult(
            nextControllerPopulation,
            controllerEvoState?.generation ?: 1
        )

        nextControllerPopulation = controllerEvoState!!.genotypes

        currentBestController = EvolvedPlayerController(controllerEvoState!!.bestPhenotype.genotype)
        return currentBestController
    }
    fun evolveDescription(numGenerations: Long, adaptControllers: Boolean = false): EvoGameRunner.FitnessWithReasons {
        val evoGameRunner = EvoGameRunner(
            { EvolvedPlayerController(currentBestController.genotype) },
            { EvolvedPlayerController(currentBestController.genotype) },
            currentBestBlocks,
            numGenerations = numGenerations,
            populationSize = gameDescriptionPopulationSize,
            csvLoggingPrefix = "coevo_$timestamp/",
            seed = random.nextLong(),
            evoProgressAccumulator = evoProgressAccumulator
        )

        if (adaptControllers) {
            println("ADAPTING CONTROLLERS")
            val fitness = { gameDescription: GameDescription ->
                val evoControllerRunner = EvoControllerRunner(
                    currentBestGameDescription,
                    { HeightBlockLevelGenerator(currentBestGameDescription, currentBestBlocks) },
                    csvLoggingPrefix = "coevo/",
                    numGenerations = 30,
                    populationSize = controllerPopulationSize,
                    seed = random.nextLong(),
                )
                val result = evoControllerRunner.evolveToResult(
                    nextControllerPopulation,
                    controllerEvoState?.generation ?: 1
                )
                val evoGameRunner2 = EvoGameRunner(
                    { EvolvedPlayerController(result.bestPhenotype.genotype) },
                    { EvolvedPlayerController(result.bestPhenotype.genotype) },
                    currentBestBlocks,
                    csvLoggingPrefix = "coevo/",
                    seed = random.nextLong()
                )
                evoGameRunner2.fitness(gameDescription)
            }
            evoGameRunner.setFitness(fitness)
        }

        gameDescriptionEvoState = evoGameRunner.evolveToResult(nextGameDescriptionPopulation, gameDescriptionEvoState?.generation ?: 1)

        nextGameDescriptionPopulation = gameDescriptionEvoState!!.genotypes
        currentBestGameDescription = EvolvedGameDescription(gameDescriptionEvoState!!.bestPhenotype.genotype)

        return evoGameRunner.fitnessWithReasoning(currentBestGameDescription)
    }

    fun currentBestTriple(): CoevolvedTriple {
        return CoevolvedTriple(currentBestBlocks, currentBestController, currentBestGameDescription)
    }

    fun runGame(maxTime: Double = -1.0) {
        val runner = CoevolutionRunner()
        if (!headless) {
            runner.runGame(currentBestTriple(), maxTime, true, evoProgressAccumulator!!.charter)
        }
    }

    override fun writeObject(oos: ObjectOutputStream): Coevolver {
        oos.writeObject(random)

        oos.writeInt(blockEvoStates.size)
        for (populations in blockEvoStates) {
            oos.writeObject(populations)
        }
        oos.writeObject(controllerEvoState)
        oos.writeObject(gameDescriptionEvoState)

        oos.writeInt(nextBlockPopulations.size)
        for (nextBlockPopulation in nextBlockPopulations) {
            oos.writeObject(nextBlockPopulation)
        }
        oos.writeObject(nextControllerPopulation)
        oos.writeObject(nextGameDescriptionPopulation)

        oos.writeInt(evolvedBlocks.size)
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

        blockEvoStates.clear()
        repeat(numBlocks) {
            blockEvoStates.add(ois.readTyped())
        }
        controllerEvoState = ois.readTyped()
        gameDescriptionEvoState = ois.readTyped()

        val numBlockPopulations = ois.readInt()
        for (i in 0 until numBlockPopulations) {
            nextBlockPopulations.addOrPut(i, ois.readTyped())
        }
        nextControllerPopulation = ois.readTyped()
        nextGameDescriptionPopulation = ois.readTyped()

        evolvedBlocks.clear()
        val numEvolvedBlocks = ois.readInt()
        repeat(numEvolvedBlocks) {
            evolvedBlocks.add(ois.readObject() as HeightBlock)
        }

        (ois.readTyped<Genotype<DoubleGene>?>())?.let { currentBestController = EvolvedPlayerController(it) }
        (ois.readTyped<Genotype<DoubleGene>?>())?.let { currentBestGameDescription = EvolvedGameDescription(it) }

        currentBestBlocks.clear()
        currentBestBlocks.addAll(
            EvoBlockRunner(currentBestGameDescription, { EvolvedPlayerController(currentBestController.genotype) }).defaultBlocks
        )
        currentBestBlocks.addAll(evolvedBlocks)

        return this
    }
}
