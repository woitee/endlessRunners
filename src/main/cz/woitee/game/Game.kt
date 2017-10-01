package cz.woitee.game

import cz.woitee.utils.TimedThread
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.game.playerControllers.PlayerController
import cz.woitee.game.collisions.GridDetectingCollisionHandler
import cz.woitee.game.descriptions.GameDescription
import cz.woitee.game.actions.abstract.HoldAction
import java.util.*

/**
 * Created by woitee on 13/01/2017.
 */

class Game(val levelGenerator: LevelGenerator, val playerController: PlayerController, val visualizer: IGameVisualizer?,
           val visualizeFrameRate: Double = 75.0, val updateRate: Double = 75.0, val mode: Mode = Mode.INTERACTIVE,
           val gameDescription: GameDescription = GameDescription(), seed: Long = Random().nextLong(), val restartOnGameOver: Boolean = true) {

    enum class Mode {
        INTERACTIVE, SIMULATION
    }

    val collHandler = GridDetectingCollisionHandler(this)
    val random = Random(seed)
    // This shows time since last update, and can be used in methods
    var updateTime = 1.0 / updateRate

    val updateThread = TimedThread({ time -> this.updateTime = time; update(time) }, updateRate, useRealTime = mode == Mode.INTERACTIVE)
    val animatorThread = if (visualizer != null) TimedThread({ visualize() }, visualizeFrameRate, useRealTime = true) else null

    var onGameOver = { if (restartOnGameOver) this.reset() else this.stop(false) }
    // keep on bottom, it should be the last variable initialized
    var gameState = GameState(this, levelGenerator)

    /**
     * Runs the game synchronously - this function will not exit until the game finishes.
     */
    fun run() {
        init()
        updateThread.run()
        println("Game finished")
        stop()
    }

    /**
     * Starts an asynchronous run of the game.
     */
    fun start() {
        init()
        updateThread.start()
    }

    /**
     * Requests to stop the game peacefully and waits until it finished.
     */
    fun stop(awaitJoin: Boolean = true) {
        updateThread.stop()
        animatorThread?.stop()
        if (awaitJoin) {
            updateThread.join()
            animatorThread?.join()
        }
    }

    fun reset() {
        levelGenerator.reset()
        playerController.reset()
        gameState = GameState(this, levelGenerator, gameState.tag)
    }

    private fun init() {
        animatorThread?.start()
    }

    private fun visualize() {
        visualizer?.update(gameState)
    }

    private fun update(time: Double) {
        val controllerOutput = playerController.onUpdate(gameState)
        if (controllerOutput != null) {
            val gameAction = controllerOutput.action
            if (controllerOutput.press && gameAction.isApplicableOn(gameState))
                gameAction.applyOn(gameState)
            else if (!controllerOutput.press && (gameAction as HoldAction?)?.canBeStoppedApplyingOn(gameState) == true) {
                gameAction.stopApplyingOn(gameState)
            }
        }

        gameState.advance(time, true)
    }
}