package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.evolution.EvoProgressAccumulator
import cz.woitee.endlessRunners.evolution.alterers.LargeBlockMutator
import cz.woitee.endlessRunners.evolution.utils.CSVPrintingPeeker
import cz.woitee.endlessRunners.evolution.utils.MyConcurrentEvaluator
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.utils.StopWatch
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

/**
 * A complete class able to run evolutionary runs of HeightBlocks.
 *
 * @param gameDescription The GameDescription to generate blocks for.
 * @param playerControllerFactory Provider of PlayerController to use for evaluating blocks.
 * @param numGenerations The limit of generations for our evolution
 * @param printStats Whether to print statistics of the running evolution to standard output.
 * @param seed The seed used for random number generation.
 */
class EvoBlockRunner(
    gameDescription: GameDescription,
    playerControllerFactory: () -> PlayerController,
    val numGenerations: Long = 25L,
    val populationSize: Int = 30,
    val printStats: Boolean = false,
    val csvLoggingPrefix: String = "",
    seed: Long = Random().nextLong(),
    val evoProgressAccumulator: EvoProgressAccumulator? = null
) : EvoBlockFitnesses(gameDescription, playerControllerFactory, seed = seed, allowHoles = true) {

    init {
        RandomRegistry.setRandom(LCG64ShiftRandom.ThreadSafe(seed))
    }

    /**
     * Default blocks, that are generally added to each game as fundamentals.
     */
    val defaultBlocks = arrayListOf(
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P  P",
                "P  P",
                "####"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "      P",
                "P     P",
                "P   ###",
                "#######"
            )
        ),
        HeightBlock(
            gameDescription,
            arrayListOf(
                "P      ",
                "P     P",
                "###   P",
                "#######"
            )
        )
    )
    var accumulatorKey = ""

    /**
     * Evolve multiple blocks for the game, trying for them to not be similiar.
     *
     * @param numBlocks How many blocks we want to generate
     * @param printStats Whether to print additional information about generated blocks
     */
    fun evolveMultipleBlocks(numBlocks: Int = 7, printStats: Boolean = false): ArrayList<HeightBlock> {
        val evolvedBlocks = ArrayList<HeightBlock>()
        evolvedBlocks.addAll(defaultBlocks)

        val stopWatch = StopWatch()
        stopWatch.start()

        for (i in 1..numBlocks) {
            accumulatorKey = "-$i"
            val block = evolveBlock(evolvedBlocks)
            if (printStats) {
                println(block)
                EvoBlockUtils.printStatistics(gameDescription, block)
            }
            if (computationStopper.shouldStop) {
                println("Terminating after generating ${i - 1} blocks")
                break
            }
            evolvedBlocks.add(block)
        }

        if (printStats) println("Evolution of blocks took ${stopWatch.stop()} ms")

        return evolvedBlocks
    }

    /**
     * Run a game with, possibly previously generated, blocks.
     *
     * @param blocks The blocks to run a game with.
     * @param playerController The player controller to use for playing.
     */
    fun runGameWithBlocks(blocks: ArrayList<HeightBlock>, playerController: PlayerController = playerControllerFactory()) {
        val visualiser: GamePanelVisualizer? = GamePanelVisualizer()
        val levelGenerator = HeightBlockLevelGenerator(gameDescription, blocks)

        val game = Game(
            levelGenerator,
            playerController,
            visualiser,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
//                updateCallback = { sleep(100) }
        )

        game.run(15000)
    }

    /**
     * Evolve a single block.
     *
     * @param otherBlocks Existing blocks. The new block will try to not be similiar to these.
     * @param startingPopulation Population of blocks to start evolving from (useful for coevolution)
     * @param startingGeneration The starting generation to evolve blocks
     */
    fun evolveBlock(
        otherBlocks: ArrayList<HeightBlock> = ArrayList(),
        startingPopulation: Iterable<Genotype<IntegerGene>>? = null,
        startingGeneration: Long = 0L
    ): HeightBlock {
        val result = evolveToResult(otherBlocks, startingPopulation, startingGeneration)
        val evoMethods = EvoBlockMethods(gameDescription, playerControllerFactory)
        return evoMethods.genotype2block(result.bestPhenotype.genotype)
    }

    /**
     * Evolve a single block, but provide whole data from the end of the evolution.
     *
     * @param otherBlocks Existing blocks. The new block will try to not be similiar to these.
     * @param startingPopulation Population of blocks to start evolving from (useful for coevolution)
     * @param startingGeneration The starting generation to evolve blocks
     */
    fun evolveToResult(
        otherBlocks: List<HeightBlock> = ArrayList(),
        startingPopulation: Iterable<Genotype<IntegerGene>>? = null,
        startingGeneration: Long = 0L
    ): EvolutionResult<IntegerGene, Int> {

        this.existingBlocks = otherBlocks

        val fitness = Function { genotype: Genotype<IntegerGene> ->
            when {
                computationStopper.shouldStop -> 0
                otherBlocks.isEmpty() -> fitness3(genotype)
                else -> fitness4(genotype)
            }
        }

        val factory = sampleGenotype()

        val engine = Engine
            .builder(fitness, factory)
            .populationSize(populationSize)
            // Setting concurrency only for fitness evaluation and not for tasks within the evaluation (such as mutation and crossover)
            .executor(Concurrency.SERIAL_EXECUTOR)
            .evaluator(MyConcurrentEvaluator<IntegerGene, Int>(ForkJoinPool.commonPool()))
            .offspringFraction(0.8)
            .maximalPhenotypeAge(1000)
            .survivorsSelector(EliteSelector(2, TournamentSelector()))
            .offspringSelector(TournamentSelector())
            .alterers(
                MultiPointCrossover(0.2),
                GaussianMutator<IntegerGene, Int>(2.0 / factory.geneCount()),
                LargeBlockMutator<IntegerGene, Int>(0.05, 1, 3, blockDimension)
            )
            .build()

        val collector = EvolutionResult.toBestEvolutionResult<IntegerGene, Int>()
        val statistics = EvolutionStatistics.ofNumber<Int>()

        var stream = if (startingPopulation == null) {
            engine.stream()
        } else {
            engine.stream(startingPopulation, startingGeneration)
        }
            .limit(numGenerations)
//                      .limit {
//                           if (it.bestFitness < bestFitness + allowedIncrement) ++fitnessUnchangedTimes
//                           bestFitness = max(it.bestFitness, bestFitness)
//                           fitnessUnchangedTimes < 100
//                        }
            .peek(statistics)
            .peek {
                evoProgressAccumulator?.addData("block$accumulatorKey", it.bestFitness.toDouble())
            }
//                        .peek {
//                            val bestGenotype = it.bestPhenotype.genotype
//                            println("${it.generation}")
//                            println(evoMethods.genotype2block(bestGenotype))
//                        }

        val csvFilePath = "out/${csvLoggingPrefix}evoBlock/${gameDescription.javaClass.simpleName}/EvoBlock_${otherBlocks.count()}others"
        val csvPeeker = CSVPrintingPeeker<Int>(csvFilePath)
        stream = stream.peek(csvPeeker)

//        if (printStats) stream = stream.peek {
//            state ->
//                println("${state.generation} - Fitness: ${state.bestPhenotype}")
//        }

        val result = stream.collect(collector)
        csvPeeker.close()
//        println(statistics)
//        val generations = statistics.evaluationDuration.count
//        print("($generations)")

//        if (printStats) println(statistics)
//        if (printStats) println()

        return result
    }

    /**
     * Print the fitness decomposition of a blocks.
     *
     * @param heightBlock The block to print for
     * @param otherBlocks Other blocks to consider when evaluating the function.
     */
    fun printFitnessValuesOfBlock(heightBlock: HeightBlock, otherBlocks: List<HeightBlock> = ArrayList()) {
        println(getFitnessValues(heightBlock, otherBlocks))
        existingBlocks = otherBlocks
        println("Total fitness is ${fitness4(heightBlock)}")
    }

    /**
     * Run a short demo of how a player progresses through a generated block.
     */
    fun demoRunBlock(heightBlock: HeightBlock) {
        val game = Game(
            FlatLevelGenerator(),
            playerControllerFactory(),
            GamePanelVisualizer(),
            75.0,
            37.5,
            gameDescription = gameDescription,
            restartOnGameOver = false
        )
        val gameState = blockValidator.getBlockAsGameState(heightBlock, game)

        val plan = blockValidator.getPlan(heightBlock)
        game.init()
        game.gameState = gameState
        try {
            for (action in plan.actions) {
                if (action != null) {
                    val button = game.gameState.buttons[action.gameButton.index]
                    val buttonAction = GameButton.StateChange(button, action.interactionType)
                    game.gameState.advanceByAction(buttonAction, game.updateTime)
                } else {
                    game.gameState.advanceByAction(null, game.updateTime)
                }

                Thread.sleep(100)
            }
        } catch (e: IndexOutOfBoundsException) {
        }
        game.stop(false)
    }
}
