package cz.woitee.endlessRunners.game

import cz.woitee.endlessRunners.utils.TimedThread
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.game.collisions.GridDetectingCollisionHandler
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import java.util.*

/**
 * Created by woitee on 13/01/2017.
 */

class Game(val levelGenerator: LevelGenerator, val playerController: PlayerController, val visualizer: GameVisualizerBase?,
           val visualizeFrameRate: Double = 75.0, val updateRate: Double = 37.5, val mode: Mode = Mode.INTERACTIVE,
           val gameDescription: GameDescription = GameDescription(), seed: Long = Random().nextLong(), val restartOnGameOver: Boolean = true,
           val updateCallback: (Game) -> Unit = { _ -> }, var freezeOnStartSeconds: Double = 0.0) {

    enum class Mode {
        INTERACTIVE, SIMULATION
    }

    var startTime = -1L
    val secondsSinceStart
        get() = (System.currentTimeMillis() - startTime).toDouble() / 1000

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
    var endedFromVisualizer = false

    /**
     * Runs the game synchronously - this function will not exit until the game finishes, or until given timeout.
     */
    fun run(timeLimitMillis: Long = -1L) {
        init()
        animatorThread?.start()
        if (timeLimitMillis == -1L) {
            updateThread.run()
            stop()
        } else {
            updateThread.start()
            updateThread.join(timeLimitMillis)
            stop()
        }
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
        startTime = System.currentTimeMillis()
        playerController.init(gameState)
        levelGenerator.init(gameState)
        inited = true
    }

    private fun visualize() {
        visualizer?.update(gameState)
    }

    fun update(time: Double) {
        assert(inited)
        if (secondsSinceStart < freezeOnStartSeconds) {
            return
        }
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