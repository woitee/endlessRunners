package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.evoController.EvoControllerRunner
import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.evolution.evoGame.EvoGameRunner
import cz.woitee.endlessRunners.evolution.evoGame.EvolvedGameDescription
import cz.woitee.endlessRunners.evolution.utils.DateUtils
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.utils.addOrPut
import cz.woitee.endlessRunners.utils.except
import io.jenetics.DoubleGene
import io.jenetics.IntegerGene
import io.jenetics.engine.EvolutionResult
import java.util.*

/**
 * A runner for the coevolution - this class contains all methods necessary to run a a full three-piece coevolution of
 * gameDescription, playerController and levelGenerator (using HeightBlocks).
 *
 * Contains some partial methods that were used for experimentation.
 */
class CoevolutionRunner(val numIterations: Int = 20, val seed: Long = Random().nextLong()) {
    /** A representation of a resulting triple of best game description, and the best controller and blocks for this description */
    data class CoevolvedTriple(val blocks: ArrayList<HeightBlock>, val controller: EvolvedPlayerController, val description: EvolvedGameDescription)

    /** Runs a phase-evolution of only blocks and controllers. Phase-evolution is similiar to coevolution, with the difference
     * of resetting each population to a random one in each iteration.
     *
     * @param gameDescription GameDescription to phase-evolve for
     * */
    fun phaseEvolveBlocksAndController(gameDescription: GameDescription) {
        val timeStamp = DateUtils.timestampString()
        // Coevolution is about evolving in phases - so we will cycle between evolving blocks and controller

        var controller = EvolvedPlayerController(EvolvedPlayerController.sampleGenotype())
        var blocks = ArrayList<HeightBlock>()

        for (i in 1..numIterations) {
            val evoBlockRunner = EvoBlockRunner(gameDescription, { EvolvedPlayerController(controller.genotype) }, csvLoggingPrefix = "phaseevo_$timeStamp/")
            blocks = evoBlockRunner.evolveMultipleBlocks()
            println("Iteration $i: blocks evaluated")
            val evoControllerRunner = EvoControllerRunner(gameDescription, { HeightBlockLevelGenerator(gameDescription, blocks) }, csvLoggingPrefix = "phaseevo_$timeStamp/")
            controller = evoControllerRunner.evolveController()
            println("Iteration $i: controller evaluated")
        }

        val evoBlockRunner = EvoBlockRunner(gameDescription, { EvolvedPlayerController(controller.genotype) })
        evoBlockRunner.runGameWithBlocks(blocks)
    }

    /** Does a small coevolution of only the blocks and controller for a given gameDescription
     *
     * @param gameDescription GameDescription to coevolve for
     * */
    fun coevolveBlocksAndController(gameDescription: GameDescription) {
        val timeStamp = DateUtils.timestampString()
        val numBlocks = 7

        val blockPopulations = ArrayList<EvolutionResult<IntegerGene, Int>>()
        val bestBlocks = ArrayList<HeightBlock>()
        var controllerResult: EvolutionResult<DoubleGene, Double>? = null
        var bestController = EvolvedPlayerController(EvolvedPlayerController.sampleGenotype())

        var allBlocks = ArrayList<HeightBlock>()

        for (i in 1..numIterations) {
            println("ITERATION $i")

            // =============== //
            // Evolving blocks //
            // =============== //

            print("evolving blocks ($numBlocks): ")

            val evoBlockRunner = EvoBlockRunner(gameDescription, { EvolvedPlayerController(bestController.genotype) }, 25, false, csvLoggingPrefix = "coevo_$timeStamp/")
            for (j in 0 until numBlocks) {
                val otherBlocks = bestBlocks.except(j)
                val blockResult = evoBlockRunner.evolveToResult(
                        otherBlocks,
                        if (i == 1) null else blockPopulations[j].genotypes,
                        if (i == 1) 0 else blockPopulations[j].generation
                )
                val block = evoBlockRunner.genotype2block(blockResult.bestPhenotype.genotype)
                bestBlocks.addOrPut(j, block)
                blockPopulations.addOrPut(j, blockResult)
                print("|")
            }
            println()
            println("Avg block fitness: ${blockPopulations.map { it.bestFitness }.average()}")

            allBlocks.clear()
            allBlocks.addAll(evoBlockRunner.defaultBlocks)
            allBlocks.addAll(bestBlocks)

            for ((j, block) in allBlocks.withIndex()) {
                println("block $j attributes: ${evoBlockRunner.getFitnessValues(block, allBlocks.except(j))}")
            }

            // =================== //
            // Evolving controller //
            // =================== //

            println("evolving controller")

            val evoControllerRunner = EvoControllerRunner(gameDescription, { HeightBlockLevelGenerator(gameDescription, allBlocks) }, csvLoggingPrefix = "coevo_$timeStamp/")
            controllerResult = evoControllerRunner.evolveToResult(
                    controllerResult?.genotypes,
                    controllerResult?.generation ?: 0
            )
            bestController = EvolvedPlayerController(controllerResult.bestPhenotype.genotype)
            println("Controller fitness: ${controllerResult.bestFitness}")
        }

        val evoBlockRunner = EvoBlockRunner(gameDescription, { bestController })
        evoBlockRunner.runGameWithBlocks(allBlocks)
    }

    /** Performs a phase evolution of all three - gameDescription, playerController and levelGenerator. Phase-evolution is similiar to coevolution, with the difference
        of resetting each population to a random one in each iteration.
     */
    fun phaseEvolveDescriptionBlocksAndController(): CoevolvedTriple {
        val timeStamp = DateUtils.timestampString()

        val bestBlocks = ArrayList<HeightBlock>()
        var bestController = EvolvedPlayerController(EvolvedPlayerController.sampleGenotype())
        var bestGameDescription = EvolvedGameDescription(EvolvedGameDescription.sampleGenotype())

        for (i in 1..numIterations) {
            println("Iteration $i: start")
            val evoBlockRunner = EvoBlockRunner(bestGameDescription, { bestController }, csvLoggingPrefix = "phaseevo_$timeStamp/")
            bestBlocks.clear()
            bestBlocks.addAll(evoBlockRunner.evolveMultipleBlocks())
            println("Iteration $i: blocks evaluated")

            val evoControllerRunner = EvoControllerRunner(bestGameDescription, { HeightBlockLevelGenerator(bestGameDescription, bestBlocks) }, csvLoggingPrefix = "phaseevo_$timeStamp/")
            bestController = evoControllerRunner.evolveController()
            println("Iteration $i: controller evaluated")

            val evoGameRunner = EvoGameRunner(
                    { EvolvedPlayerController(bestController.genotype) },
                    { EvolvedPlayerController(bestController.genotype) },
                    csvLoggingPrefix = "phaseevo_$timeStamp/"
            )
            bestGameDescription = evoGameRunner.evolveGame()
            println("Iteration $i: game evaluated")
        }

        return CoevolvedTriple(bestBlocks, bestController, bestGameDescription)
    }

    /** This runs the main coevolution of gameDescriptions, playerControllers and levelGenerators.
     * Returns a triple of best-game description and best players and best levelGenerators for it.
     * */
    fun coevolveDescriptionBlocksAndController(): CoevolvedTriple {
        val random = Random(seed)
        val timeStamp = DateUtils.timestampString()
        val numBlocks = 7

        val blockPopulations = ArrayList<EvolutionResult<IntegerGene, Int>>()
        val bestBlocks = ArrayList<HeightBlock>()
        val allBlocks = ArrayList<HeightBlock>()

        var controllerPopulation: EvolutionResult<DoubleGene, Double>? = null
        var bestController = EvolvedPlayerController(EvolvedPlayerController.sampleGenotype())

        var gameDescriptionPopulation: EvolutionResult<DoubleGene, Double>? = null
        var bestGameDescription = EvolvedGameDescription(EvolvedGameDescription.sampleGenotype())

        for (i in 1..numIterations) {

            println("ITERATION $i")

            // =============== //
            // Evolving blocks //
            // =============== //

            print("evolving blocks ($numBlocks): ")

            val evoBlockRunner = EvoBlockRunner(
                    bestGameDescription,
                    { EvolvedPlayerController(bestController.genotype) },
                    15,
                    false,
                    csvLoggingPrefix = "coevo_$timeStamp/",
                    seed = random.nextLong()
            )
            for (j in 0 until numBlocks) {
                val otherBlocks = bestBlocks.except(j)
                val blockResult = evoBlockRunner.evolveToResult(
                        otherBlocks,
                        if (i == 1) null else blockPopulations[j].genotypes,
                        if (i == 1) 0 else blockPopulations[j].generation
                )
                val block = evoBlockRunner.genotype2block(blockResult.bestPhenotype.genotype)
                bestBlocks.addOrPut(j, block)
                blockPopulations.addOrPut(j, blockResult)
                print("|")
            }
            println()
            println("Avg block fitness: ${blockPopulations.map { it.bestFitness }.average()}")

            allBlocks.clear()
            allBlocks.addAll(evoBlockRunner.defaultBlocks)
            allBlocks.addAll(bestBlocks)

            for ((j, block) in allBlocks.withIndex()) {
                println("block $j attributes: ${evoBlockRunner.getFitnessValues(block, allBlocks.except(j))}")
            }

            // =================== //
            // Evolving controller //
            // =================== //

            println("evolving controller")

            val evoControllerRunner = EvoControllerRunner(
                    bestGameDescription,
                    { HeightBlockLevelGenerator(bestGameDescription, allBlocks) },
                    csvLoggingPrefix = "coevo_$timeStamp/",
                    numGenerations = 50,
                    seed = random.nextLong()
            )
            controllerPopulation = evoControllerRunner.evolveToResult(
                    controllerPopulation?.genotypes,
                    controllerPopulation?.generation ?: 0
            )
            bestController = EvolvedPlayerController(controllerPopulation.bestPhenotype.genotype)
            println("Controller fitness: ${controllerPopulation.bestFitness}")

            // ========================= //
            // Evolving game description //
            // ========================= //

            if (i == numIterations) {
                println("Not evolving game description in the last iteration, we want to end with best controller and blocks for a given game")
            } else {
                println("Evolving game description")

                val evoGameRunner = EvoGameRunner(
                        { EvolvedPlayerController(bestController.genotype) },
                        { EvolvedPlayerController(bestController.genotype) },
                        allBlocks,
                        numGenerations = 10,
                        csvLoggingPrefix = "coevo_$timeStamp/",
                        seed = random.nextLong()
                )
                gameDescriptionPopulation = evoGameRunner.evolveToResult(gameDescriptionPopulation?.genotypes, gameDescriptionPopulation?.generation ?: 0)
                bestGameDescription = EvolvedGameDescription(gameDescriptionPopulation.bestPhenotype.genotype)

                println("Game Description fitness: ${gameDescriptionPopulation.bestFitness}")
            }
        }

        println("Coevolution finished")
        return CoevolvedTriple(bestBlocks, bestController, bestGameDescription)
    }

    /**
     * Runs the coevolved triple to observe its behaviour.
     *
     * @triple the result to run
     * @seconds time to run for. If negative, the game runs endlessly.
     */
    fun runGame(triple: CoevolvedTriple, seconds: Double = -1.0) {
        runGame(triple.description, triple.blocks, triple.controller, seconds)
    }

    /**
     * Runs a selected combination of game description, blocks and player controller for a specified amount of time
     *
     * @param gameDescription The GameDescription to use
     * @parem bestBlocks The blocks for level generation (default ones will be added)
     * @param playerController The PlayerController to use for running
     * @param seconds Time to run for. If negative, will run endlessly.
     */
    fun runGame(gameDescription: GameDescription, bestBlocks: ArrayList<HeightBlock>, playerController: PlayerController, seconds: Double = -1.0) {
        val evoBlockRunner = EvoBlockRunner(gameDescription, { NoActionPlayerController() })
        val allBlocks = ArrayList<HeightBlock>(bestBlocks)
        allBlocks.addAll(evoBlockRunner.defaultBlocks)

        val gamePanelVisualizer = GamePanelVisualizer(timeProportionedDrawing = false, debugging = true)

        val game = Game(
                HeightBlockLevelGenerator(gameDescription, allBlocks),
                playerController,
                gamePanelVisualizer,
                gameDescription = gameDescription
                // uncomment for screenshots
//                updateCallback = {
//                    val gameState = it.gameState
//                    var maxPlayerX = 0.0
//                    val actionList = ArrayList<GameButton.StateChange?>()
//                    val undoList = ArrayList<IUndo>()
//                    gamePanelVisualizer.debugObjects.clear()
//                    while (!(gameState.isGameOver || gameState.isPlayerAtEnd())) {
//                        val action = playerController.onUpdate(gameState)
//                        actionList.add(action)
//                        val undo = gameState.advanceUndoableByAction(action)
//                        undoList.add(undo)
//                        if (undoList.count() % 5 == 1)
//                            gamePanelVisualizer.debugObjects.add(gameState.player.makeCopy())
//                        if (gameState.player.x > maxPlayerX) maxPlayerX = gameState.player.x
//                    }
//                    for (undo in undoList.reversed()) {
//                        undo.undo(gameState)
//                    }
//                    ScreenshotSaver.saveScreenshot(gameState, gamePanelVisualizer, "pics/${tag}_${Timestamp(System.currentTimeMillis())}.png")
//                }
        )

        game.run(if (seconds > 0) (seconds * 1000).toLong() else -1L)
    }
}
