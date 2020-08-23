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
import cz.woitee.endlessRunners.game.playerControllers.wrappers.DisplayingWrapper
import java.util.*

/**
 * A runner for the coevolution - this class contains all methods necessary to run a a full three-piece coevolution of
 * gameDescription, playerController and levelGenerator (using HeightBlocks).
 *
 * Contains some partial methods that were used for experimentation.
 */
class CoevolutionRunner(val numIterations: Int = 20, val seed: Long = Random().nextLong()) {
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
        val coevolver = Coevolver(seed)
        val numBlocks = 7

        for (i in 1..numIterations) {
            println("ITERATION $i")

            print("evolving blocks ($numBlocks): ")
            coevolver.evolveBlocks(30, numBlocks, 30, true)

            println("evolving controller")
            coevolver.evolveController(50, 50)
            println("Controller fitness: ${coevolver.controllerPopulation!!.bestFitness}")
        }

        val evoBlockRunner = EvoBlockRunner(gameDescription, { coevolver.currentBestController })
        evoBlockRunner.runGameWithBlocks(coevolver.currentBestBlocks)
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
        val numBlocks = 7
        val coevolver = Coevolver(seed)

        for (i in 1..numIterations) {

            println("ITERATION $i")

            // =============== //
            // Evolving blocks //
            // =============== //

            print("evolving blocks ($numBlocks): ")
            coevolver.evolveBlocks(30, 30, numBlocks, true)

            // =================== //
            // Evolving controller //
            // =================== //

            println("Evolving controller")
            coevolver.evolveController(50, 50)
            println("Controller fitness: ${coevolver.controllerPopulation!!.bestFitness}")

            // ========================= //
            // Evolving game description //
            // ========================= //

            if (i == numIterations) {
                println("Not evolving game description in the last iteration, we want to end with best controller and blocks for a given game")
            } else {
                println("Evolving game description")
                coevolver.evolveDescription(20, 50)
                println("Game Description fitness: ${coevolver.gameDescriptionPopulation!!.bestFitness}")
            }
        }

        println("Coevolution finished")
        return coevolver.currentBestTriple()
    }

    /**
     * Runs the coevolved triple to observe its behaviour.
     *
     * @triple the result to run
     * @seconds time to run for. If negative, the game runs endlessly.
     */
    fun runGame(triple: CoevolvedTriple, seconds: Double = -1.0, visualizeActions: Boolean = true) {
        val controller = if (visualizeActions) DisplayingWrapper(triple.controller) else triple.controller
        runGame(triple.description, triple.blocks, controller, seconds)
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
