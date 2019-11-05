package cz.woitee.endlessRunners.evolution.evoController

import cz.woitee.endlessRunners.evolution.utils.CSVPrintingPeeker
import cz.woitee.endlessRunners.evolution.utils.MyConcurrentEvaluator
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.game.tracking.GameStateTracking
import cz.woitee.endlessRunners.game.tracking.TrackingUtils
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import io.jenetics.internal.util.Concurrency
import io.jenetics.prngine.LCG64ShiftRandom
import io.jenetics.util.RandomRegistry
import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.function.Function
import kotlin.math.roundToLong

/**
 * A class for running neuroevolutions of PlayerControllers.
 *
 * @param gameDescription The game we will evolve players for
 * @param levelGeneratorFactory A provider of level generators we will use
 * @param debugPrints Whether to print statistics on screen during our run
 * @param numGenerations The limit of generations to run for
 * @param numIndividuals The number of individuals to use
 * @param csvLoggingPrefix Prefix of the file for storing CSV logs
 * @param seed Seed for the random number generator (usable for reproducibility)
 */
class EvoControllerRunner(
    val gameDescription: GameDescription,
    val levelGeneratorFactory: () -> LevelGenerator,
    var debugPrints: Boolean = false,
    val numGenerations: Long = 200L,
    val numIndividuals: Int = 50,
    val csvLoggingPrefix: String = "",
    val seed: Long = Random().nextLong()
) {
    /** number of game updates for each fitness evalutaion */
    val fitnessGameplayUpdates = 10000

    val myEvaluator = MyConcurrentEvaluator<DoubleGene, Double>(ForkJoinPool.commonPool(), true, seed = seed)

    init {
        RandomRegistry.setRandom(LCG64ShiftRandom.ThreadSafe(seed))
    }

    /**
     * Evolve a player controller.
     *
     * @param startingPopulation The starting population of controller, leave blank for autoinitialization.
     * @param startingGeneration The starting generation of controller, 0 by default.
     */
    fun evolveController(startingPopulation: Iterable<Genotype<DoubleGene>>? = null, startingGeneration: Long = 0L): EvolvedPlayerController {
        val result = evolveToResult(startingPopulation, startingGeneration)
        return EvolvedPlayerController(result.bestPhenotype.genotype)
    }

    /**
     * Evolves a player controller, but returns a complete information about the end of the evolution.
     *
     * @param startingPopulation The starting population of controller, leave blank for autoinitialization.
     * @param startingGeneration The starting generation of controller, 0 by default.
     */
    fun evolveToResult(startingPopulation: Iterable<Genotype<DoubleGene>>? = null, startingGeneration: Long = 0L): EvolutionResult<DoubleGene, Double> {
        val genotype = EvolvedPlayerController.sampleGenotype()
        val engine = Engine
                .builder(fitness, genotype)
                // Setting concurrency only for fitness evaluation and not for tasks within the evaluation (such as mutation and crossover)
                // Also using a custom evaluator to distribute random values consistently
                .executor(Concurrency.SERIAL_EXECUTOR)
                .evaluator(myEvaluator)
                .optimize(Optimize.MINIMUM)
                .populationSize(numIndividuals)
                .offspringFraction(0.8)
                .maximalPhenotypeAge(1000)
                .survivorsSelector(EliteSelector(5, TournamentSelector()))
                .offspringSelector(TournamentSelector())
                .alterers(
                        MultiPointCrossover(0.3),
                        GaussianMutator<DoubleGene, Double>(0.05),
                        GaussianMutator<DoubleGene, Double>(0.005)
                )
                .build()

        val statistics = EvolutionStatistics.ofNumber<Double>()
        val collector = EvolutionResult.toBestEvolutionResult<DoubleGene, Double>()

        val csvPeeker = CSVPrintingPeeker<Double>("out/${csvLoggingPrefix}evoController/${gameDescription.javaClass.simpleName}/EvoController_")

        val stream = if (startingPopulation == null) {
            engine.stream()
        } else {
            engine.stream(startingPopulation, startingGeneration)
        }

        var stream2 = stream
            .limit(numGenerations)
            .peek { result ->
                if (debugPrints) {
                    val best = result.bestPhenotype
                    println("Generation ${result.generation}: The fitness is ${best.fitness}")
                }
            }.peek(statistics)

        stream2 = stream2.peek(csvPeeker)
        val result = stream2
                .peek(statistics)
                .collect(collector)

        return result
    }

    /**
     * Runs a game that matches the setting of the neuroevolution. Usable to observe a freshly evolved player controller
     * behaving.
     *
     * @param controller Controller to run the game width.
     * @param timeLimitSeconds Limit of time for the game run.
     */
    fun runGame(controller: PlayerController, timeLimitSeconds: Double = -1.0) {
        val game = Game(
                levelGeneratorFactory(), controller, GamePanelVisualizer(),
                gameDescription = gameDescription
        )

        game.run((timeLimitSeconds * 1000).roundToLong())
    }

    /**
     * Fitness function of the individual.
     */
    val fitness = Function<Genotype<DoubleGene>, Double> { genotype -> fitness(genotype) }
    fun fitness(genotype: Genotype<DoubleGene>): Double {
        val controller = EvolvedPlayerController(genotype)
        val seed = myEvaluator.seedForGenotype(genotype)
        return fitness(controller, seed)
    }
    fun fitness(controller: PlayerController, seed: Long? = null): Double {
        val gameDescriptionCopy = gameDescription.makeCopy()
        val gameDescriptionTracking = TrackingUtils.addTracking(gameDescriptionCopy)
        val game = Game(
                levelGeneratorFactory(), controller, null,
                gameDescription = gameDescriptionCopy,
                mode = Game.Mode.SIMULATION,
                seed = seed ?: Random().nextLong()
//                seed = 1L
        )

        val gameStateTracking = GameStateTracking()

        // running the game manually
        game.init()
        for (i in 1..fitnessGameplayUpdates) {
            game.update()
            gameStateTracking.addSnapshot(game.gameState)
        }

        var totalActionsUsage = 0
        for (trackedAction in gameDescriptionTracking.actions) {
            totalActionsUsage += trackedAction.timesUsed * 10
        }
        for (trackedAction in gameDescriptionTracking.holdActions) {
            totalActionsUsage += trackedAction.timesStarted * 3 + trackedAction.timesKeptHeld
        }

//        println("${genotype.get(0, 0)} ${myEvaluator.seedForGenotype(genotype)} -> $fitness {${gameStateTracking.numInits}, ${totalActionsUsage}}")
        return (gameStateTracking.numInits * 500 + totalActionsUsage).toDouble()
    }
}
