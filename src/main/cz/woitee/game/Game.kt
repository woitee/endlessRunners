package cz.woitee.game

import cz.woitee.utils.TimedThread
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.game.playerControllers.PlayerController
import cz.woitee.game.collisions.GridDetectingCollisionHandler
import cz.woitee.game.descriptions.GameDescription
import java.util.*

/**
 * Created by woitee on 13/01/2017.
 */

class Game(val levelGenerator: LevelGenerator, val playerController: PlayerController, val visualizer: GameVisualizerBase?,
           val visualizeFrameRate: Double = 75.0, val updateRate: Double = 75.0, val mode: Mode = Mode.INTERACTIVE,
           val gameDescription: GameDescription = GameDescription(), seed: Long = Random().nextLong(), val restartOnGameOver: Boolean = true,
           val updateCallback: (Game) -> Unit = { _ -> }) {

    enum class Mode {
        INTERACTIVE, SIMULATION
    }

    val collHandler = GridDetectingCollisionHandler(this)
    val random = Random(seed)
    // This shows time since last update, and can be used in methods
    var updateTime = 1.0 / updateRate

    val updateThread = TimedThread({ update(updateTime) }, updateRate, useRealTime = mode == Mode.INTERACTIVE)
    val animatorThread = if (visualizer != null) TimedThread({ visualize() }, visualizeFrameRate, useRealTime = true) else null

    var onGameOver = { if (restartOnGameOver) this.reset() else this.stop(false) }
    // keep on bottom, it should be the last variable initialized
    var gameState = GameState(this, levelGenerator)

    protected var inited = false

    /**
     * Runs the game synchronously - this function will not exit until the game finishes.
     */
    fun run() {
        init()
        animatorThread?.start()
        updateThread.run()
        println("Game finished")
        stop()
    }

    /**
     * Starts an asynchronous run of the game.
     */
    fun start() {
        init()
        animatorThread?.start()
        updateThread.start()
    }

    /**
     * Requests to dispose the game peacefully and waits until it finished.
     */
    fun stop(awaitJoin: Boolean = true) {
        updateThread.stop()
        animatorThread?.stop()
        if (awaitJoin) {
            updateThread.join()
            animatorThread?.join()
        }
        visualizer?.dispose()
    }

    fun reset() {
        inited = false
        gameState = GameState(this, levelGenerator, gameState.tag)
        init()
    }

    fun init() {
        playerController.init(gameState)
        levelGenerator.init(gameState)
        inited = true
    }

    private fun visualize() {
        visualizer?.update(gameState)
    }

    fun update(time: Double) {
        assert(inited)
        // Get the action that should be performed
        val controllerAction = playerController.onUpdate(gameState)
        // Update the GameState by it
        gameState.advanceByAction(controllerAction, time)
        // Notify the levelGenerator of the update
        levelGenerator.onUpdate(time, controllerAction, gameState)
        // Scroll the screen, possibly asking the LevelGenerator for new content
        gameState.scroll(time)

        updateCallback(this)

        if (gameState.isGameOver) {
            onGameOver()
        }
    }
}