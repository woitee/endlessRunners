package cz.woitee.endlessRunners.game

import cz.woitee.endlessRunners.game.collisions.GridDetectingCollisionHandler
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.LevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.utils.TimedThread
import java.util.*

/**
 * The main Game object, holding everything that exists in a game. Only acts as a "middle man" between various components,
 * such as visualization, level generation, the player controller, etc.
 *
 * Also runs the important threads if the game is playing.
 *
 * Contains one GameState, where the whole state is kept.
 *
 * @param levelGenerator The level generator for the game.
 * @param playerController The player controller for the game.
 * @param visualizer Vizualization for the game
 * @param visualizeFrameRate target FPS for the vizualizer
 * @param updateRate target FPS for the update thread
 * @param mode INTERACTIVE if we should wait after each update, to provide smooth experience or SIMULATION if we want to
 *  perform updates as fast as possible
 * @param gameDescription The description of the game.
 * @param seed The seed for the main RNG of the game.
 * @param restartOnGameOver Whether the game restarts on a game over, or ends.
 * @param updateCallback A callback happening each update.
 * @param freezeOnStartSeconds How long should the player wait before getting into a new game.
 */

class Game(
    val levelGenerator: LevelGenerator,
    val playerController: PlayerController,
    val visualizer: GameVisualizerBase?,
    val visualizeFrameRate: Double = 75.0,
    val updateRate: Double = 37.5,
    val mode: Mode = Mode.INTERACTIVE,
    val gameDescription: GameDescription = GameDescription(),
    seed: Long = Random().nextLong(),
    val restartOnGameOver: Boolean = true,
    val updateCallback: (Game) -> Unit = { _ -> },
    var freezeOnStartSeconds: Double = 0.0
) {

    enum class Mode {
        INTERACTIVE, SIMULATION
    }

    var startTime = -1L
    val secondsSinceStart
        get() = (System.currentTimeMillis() - startTime).toDouble() / 1000

    /**
     * The object detecting and evaluating collisions in the game.
     */
    val collHandler = GridDetectingCollisionHandler(this)
    /** Main RNG of the game, which all of the actions should use */
    val random = Random(seed)
    // This shows time since last update, and can be used in methods
    var updateTime = 1.0 / updateRate

    val updateThread = TimedThread({ update(updateTime) }, updateRate, useRealTime = mode == Mode.INTERACTIVE)
    val animatorThread = if (visualizer != null) TimedThread({ visualize() }, visualizeFrameRate, useRealTime = true) else null

    /**
     * What happens when a game over happens.
     */
    var onGameOver = { if (restartOnGameOver) this.reset() else this.stop(false) }
    // keep on bottom, it should be the last variable initialized
    /**
     * GameState, where the complete state is kept.
     */
    var gameState = GameState(this, levelGenerator)

    /**
     * Whether the game is currently running.
     */
    val running: Boolean
        get() = updateThread.running

    protected var inited = false
    /**
     * If the visualizer ended this game.
     */
    var endedFromVisualizer = false

    /**
     * Runs the game synchronously - this function will not exit until the game finishes, or until given timeout.
     */
    fun run(timeLimitMillis: Long = -1L) {
        init()
        if (timeLimitMillis < 0) {
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

    /**
     * Resets the game immediately.
     */
    fun reset() {
        inited = false
        gameState = GameState(this, levelGenerator, gameState.tag)
        init()
    }

    /**
     * Initializes the game. If updating manually, this has to be done before calling update.
     */
    fun init() {
        startTime = System.currentTimeMillis()
        playerController.init(gameState)
        levelGenerator.init(gameState)
        if (animatorThread?.running == false) animatorThread.start()
        inited = true
    }

    private fun visualize() {
        visualizer?.update(gameState)
    }

    /**
     * Performs one update frame. Is usually called asynchronously from the updateThread, but can also be used for
     * manual execution performed frame by frame.
     */
    fun update(time: Double = this.updateTime) {
        assert(inited)
        if (secondsSinceStart < freezeOnStartSeconds && mode == Mode.INTERACTIVE) {
            return
        }
        // Get the action that should be performed
        val controllerAction = playerController.onUpdate(gameState)
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
