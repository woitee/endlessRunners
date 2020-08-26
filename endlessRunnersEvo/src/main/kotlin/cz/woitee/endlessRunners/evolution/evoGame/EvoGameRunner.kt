package cz.woitee.endlessRunners.evolution.evoGame

import cz.woitee.endlessRunners.evolution.EvoProgressAccumulator
import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.utils.CSVPrintingPeeker
import cz.woitee.endlessRunners.evolution.utils.DateUtils
import cz.woitee.endlessRunners.evolution.utils.MyConcurrentEvaluator
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.game.tracking.GameDescriptionTracking
import cz.woitee.endlessRunners.game.tracking.GameStateTracking
import cz.woitee.endlessRunners.game.tracking.TrackingUtils
import cz.woitee.endlessRunners.utils.ComputationStopper
import cz.woitee.endlessRunners.utils.StopWatch
import cz.woitee.endlessRunners.utils.fileWithCreatedPath
import cz.woitee.endlessRunners.utils.format
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import io.jenetics.internal.util.Concurrency
import io.jenetics.prngine.LCG64ShiftRandom
import io.jenetics.util.RandomRegistry
import java.io.FileWriter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ForkJoinPool
import java.util.function.Function
import kotlin.random.Random
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter

/**
 * A class facilitating the runs of evolutions of the game rules.
 *
 * @param playerControllerFactoryForBlocks Provider of playerControllers for generating HeightBlocks
 * @param playerControllerFactoryForGameRun Provider of playerControllers for evaluating longer parts of a game
 * @param blocks Blocks that should be used in the levels, instead of being generated
 * @param debugPrints Whether to print info during an evolutionary run
 * @param numGenerations The number of generations to evolve for
 * @param populationSize Population size for the evolutionary run
 * @param csvLoggingPrefix Prefix for resulting csv files.
 * @param seed Seed for the random generator.
 */
class EvoGameRunner(
    val playerControllerFactoryForBlocks: (ComputationStopper) -> PlayerController,
    val playerControllerFactoryForGameRun: (ComputationStopper) -> PlayerController,
    val blocks: ArrayList<HeightBlock>? = null,
    val debugPrints: Boolean = false,
    val numGenerations: Long = 50,
    val populationSize: Int = 50,
    val csvLoggingPrefix: String = "",
    val seed: Long = Random.Default.nextLong(),
    val evoProgressAccumulator: EvoProgressAccumulator? = null
) {

    /**
     * Class representing atomic parts of the fitness values.
     */
    class FitnessValues(
        val gameDescription: GameDescription,
        val playerControllerFactoryForBlocks: (ComputationStopper) -> PlayerController,
        val playerControllerFactoryForGameRun: (ComputationStopper) -> PlayerController,
        val blocks: ArrayList<HeightBlock>? = null,
        val computationStopper: ComputationStopper = ComputationStopper(),
        val seed: Long = Random.Default.nextLong()
    ) {
        val evolvedBlocks = ArrayList<HeightBlock>()
        var averageBlockFitness = 0.0
        val gameplayUpdates = 1000
        val gameplayStats = GameStateTracking()
        val rng = Random(seed)
        lateinit var gameDescriptionTracking: GameDescriptionTracking

        init {
            evolveBlocks()
            runGameSimulation()
        }

        /**
         * Evolve blocks for as the first component of fitness evaluation.
         */
        private fun evolveBlocks() {
            // Evolve blocks and count their average fitness
            val runner = EvoBlockRunner(gameDescription, { playerControllerFactoryForBlocks(computationStopper) }, seed = rng.nextLong())

            val blocks = blocks ?: runner.evolveMultipleBlocks()
            evolvedBlocks.addAll(blocks)

            averageBlockFitness = evolvedBlocks.stream().mapToDouble { runner.fitness3(it).toDouble() }.average().orElse(0.0)
        }

        /**
         * Run simulation for a predetermined number of steps as the second component of the fitness.
         */
        private fun runGameSimulation() {
            val stopWatch = StopWatch()
            // Run game for a given number of updates and calculate fitness from it
            stopWatch.start()
            gameDescriptionTracking = TrackingUtils.addTracking(gameDescription)
            val playerController = playerControllerFactoryForGameRun(computationStopper)
            val game = Game(
                    gameDescription = gameDescription,
                    levelGenerator = HeightBlockLevelGenerator(gameDescription, evolvedBlocks),
                    visualizer = null,
                    playerController = playerController,
                    mode = Game.Mode.SIMULATION,
                    seed = rng.nextLong()
            )

            game.init()
//            game.animatorThread?.start()
            for (i in 1..gameplayUpdates) {
                if (computationStopper.shouldStop) {
                    println("Terminated game simulation after ${i - 1} gamePlayUpdates")
                    gameplayStats.clear()
                    gameplayStats.numInits = gameplayUpdates
                    break
                }
                game.update(game.updateTime)
//                Thread.sleep((game.updateTime * 1000).toLong())
                gameplayStats.addSnapshot(game.gameState)
            }
//            println("${Thread.currentThread().id} Running the game took ${stopWatch.stop()} ms")
        }
    }

    /**
     * A helper class to monitor running fitness evaluations.
     */
    data class ThreadComputationStopper(val startMillis: Long, val computationStopper: ComputationStopper)
    class FitnessWithReasons {
        var value = 0.0
        private val reasons = ArrayList<String>()

        val reasoning: String
            get() = reasons.joinToString("; ")

        fun award(amount: Double, reason: String) {
            value += amount
            reasons.add("$amount for $reason")
        }

        fun printReasoning() {
            println("FITNESS REASONING")
            reasons.forEach { println(it) }
            println()
        }
    }

    val myEvaluator = MyConcurrentEvaluator<DoubleGene, Double>(ForkJoinPool.commonPool(), true, seed = seed)

    val runningFitnessComputations = ConcurrentHashMap<Long, ThreadComputationStopper>()
    val samplePlayerControllerForBlocks = playerControllerFactoryForBlocks(ComputationStopper())
    val samplePlayerControllerForGameRun = playerControllerFactoryForGameRun(ComputationStopper())

    val fileWriter = FileWriter(fileWithCreatedPath("out/fitnesses.csv"), true)
    val csvPrinter = CSVPrinter(fileWriter, CSVFormat.DEFAULT)

    init {
        RandomRegistry.setRandom(LCG64ShiftRandom.ThreadSafe(seed))
        runMonitoringThread()
    }

    /**
     * Starts a monitoring threads, that ends computations in evaluations after a certain time passes.
     */
    protected fun runMonitoringThread() {
        Thread {
            while (true) {
                val currentMillis = System.currentTimeMillis()
                for ((threadId, threadComputation) in runningFitnessComputations) {
                    val runningSeconds = (currentMillis - threadComputation.startMillis).toDouble() / 1000
                    if (runningSeconds > 60) {
                        println("Fitness evaluation in ThreadID:$threadId is already running for ${runningSeconds.format(2)}s")
                    }
                    if (runningSeconds > 120) {
                        println("Terminating computation in thread $threadId")
                        threadComputation.computationStopper.stop()
                    }
                }
                Thread.sleep(30000)
            }
        }.start()
    }

    /**
     * Returns the fitness of a genotype.
     */
    val fitness = Function<Genotype<DoubleGene>, Double> { genotype -> fitness(genotype) }
    fun fitness(genotype: Genotype<DoubleGene>): Double {
        val limitForDFS = samplePlayerControllerForBlocks is DFSPlayerController || samplePlayerControllerForGameRun is DFSPlayerController

        val gameDescription = EvolvedGameDescription(
                genotype, limitForDFS
        )

        val fitness = fitness(gameDescription)

        csvPrinter.printRecord(genotype, gameDescription, fitness)
        csvPrinter.flush()

        return fitness
    }

    fun fitness(gameDescription: GameDescription): Double {
        return fitnessWithReasoning(gameDescription).value
    }

    fun fitnessWithReasoning(gameDescription: GameDescription): FitnessWithReasons {
//        println("${Thread.currentThread().id} evaluating $gameDescription")
        val threadId = Thread.currentThread().id
        val computationStopper = ComputationStopper()
        runningFitnessComputations[threadId] = ThreadComputationStopper(System.currentTimeMillis(), computationStopper)
        val fitnessValues = FitnessValues(
                gameDescription,
                playerControllerFactoryForBlocks,
                playerControllerFactoryForGameRun,
                blocks = blocks,
                computationStopper = computationStopper,
                seed = seed
        )

        runningFitnessComputations.remove(threadId)

//        println(gameDescription.toString())
//        println(fitnessValues.gameDescriptionTracking)
//        println(fitnessValues.gameplayStats)

        val fitness = FitnessWithReasons()

        fitness.award(fitnessValues.averageBlockFitness, "average block fitness")

        with(fitnessValues.gameplayStats) {
            if (timeAirborne in 200..800) fitness.award(500.0, "reasonable time airborne ($timeAirborne)")
            if (timeOutOfScreen < 100) fitness.award(500.0, "low time spent out of screen ($timeOutOfScreen)")
            for ((shape, time) in timeInOtherDimensions) {
                if (time > 10) fitness.award(500.0, "being in dimensions (${shape.x}, ${shape.y})")
            }
            fitness.award(-100.0 * (numInits - 1), "number of restarts (${numInits - 1})")
        }

        with(fitnessValues.gameDescriptionTracking) {
            for (action in actions) {
                if (action.timesUsed >= 2) fitness.award(200.0, "using action $action")
            }
            for (holdAction in holdActions) {
                if (holdAction.timesStarted >= 2) fitness.award(200.0, "using action $holdAction")
            }
            for (effect in effects) {
                if (effect.timesApplied >= 2) fitness.award(100.0, "applying effect $effect")
            }
            for (collisionEffect in collisionEffects) {
                if (collisionEffect.timesApplied >= 2) fitness.award(40.0, "applying collision effect $collisionEffect")
            }
            for (condition in conditions) {
                if (condition.trueEvaluations >= 1 && condition.falseEvaluations >= 1) fitness.award(100.0, "$condition evaluated as both true and false")
            }
        }

        return fitness
    }

    /**
     * Performs one run of a GameDescription evolution, returning the best GameDescription at the end.
     */
    fun evolveGame(
        startingPopulation: Iterable<Genotype<DoubleGene>>? = null,
        startingGeneration: Long = 0L
    ): EvolvedGameDescription {

        val result = evolveToResult(startingPopulation, startingGeneration)
        return EvolvedGameDescription(result.bestPhenotype.genotype)
    }

    /**
     * Performs one run of a GameDescription evolution, returning complete data about the state at the end.
     */
    fun evolveToResult(
        startingPopulation: Iterable<Genotype<DoubleGene>>? = null,
        startingGeneration: Long = 0L
    ): EvolutionResult<DoubleGene, Double> {

        val csvPeeker = CSVPrintingPeeker<Double>("out/${csvLoggingPrefix}evoGame/EvoGame_$seed")

        val engine = Engine
                .builder(fitness, EvolvedGameDescription.sampleGenotype())
                .populationSize(populationSize)
                .executor(Concurrency.SERIAL_EXECUTOR)
                .evaluator(myEvaluator)
                .offspringFraction(0.8)
                .maximalPhenotypeAge(1000)
                .survivorsSelector(TournamentSelector())
                .offspringSelector(TournamentSelector())
                .alterers(
                        // 81 genes
                        MultiPointCrossover(0.1),
//                        SwapMutator(0.01)
                        GaussianMutator<DoubleGene, Double>(1.0 / 81),
                        GaussianMutator<DoubleGene, Double>(0.05)
                )
                .build()

        val collector = EvolutionResult.toBestEvolutionResult<DoubleGene, Double>()
        val statistics = EvolutionStatistics.ofNumber<Double>()

        val stream = if (startingPopulation == null) {
            engine.stream()
        } else {
            engine.stream(startingPopulation, startingGeneration)
        }

        val result = stream
                .limit(numGenerations)
                .peek { result ->
                    val best = result.bestPhenotype
//                    println(EvolvedGameDescription(best.genotype))
                    println("${DateUtils.timestampString()} Generation ${result.generation}: The fitness is ${best.fitness}")
                }
                .peek(csvPeeker)
                .peek(statistics)
                .peek { evoProgressAccumulator?.addData("game", it.bestFitness.toDouble()) }
                .collect(collector)

        csvPeeker.close()

        return result
    }

    /**
     * Runs the game with a genotype using the same conditions as in the fitness function, usable to evaluate a just finished run of evolution.
     */
    fun runGame(genotype: Genotype<DoubleGene>, controller: PlayerController = playerControllerFactoryForGameRun(ComputationStopper())) {
        runGame(EvolvedGameDescription(genotype), controller)
    }

    /**
     * Runs the game with a gameDescription using the same conditions as in the fitness function, usable to evaluate a just finished run of evolution.
     */
    fun runGame(gameDescription: EvolvedGameDescription, controller: PlayerController = playerControllerFactoryForGameRun(ComputationStopper())) {
        println("Running game with description")
        println(gameDescription)
        val runner = EvoBlockRunner(gameDescription, { playerControllerFactoryForBlocks(ComputationStopper()) })
        val evolvedBlocks = runner.evolveMultipleBlocks(10, true)

        runner.runGameWithBlocks(evolvedBlocks, controller)
    }
}
