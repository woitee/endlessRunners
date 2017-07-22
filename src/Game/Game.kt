package Game

import Utils.TimedThread
import Game.PCG.ILevelGenerator
import Game.PlayerControllers.PlayerController
import Game.Collisions.BaseCollisionHandler
import Game.Collisions.GridDetectingCollisionHandler
import java.util.*

/**
 * Created by woitee on 13/01/2017.
 */

class Game(val levelGenerator: ILevelGenerator, val playerController: PlayerController, val visualizer: IGameVisualizer?,
           val visualizeFrameRate: Double = 75.0, val updateRate: Double = 75.0, val mode: Mode = Mode.INTERACTIVE,
           seed: Long = Random().nextLong()) {

    enum class Mode {
        INTERACTIVE, SIMULATION
    }

    val gameDescription = GameDescription()
    val collHandler = GridDetectingCollisionHandler(this)
    var gameState = GameState(this, levelGenerator)
    val random = Random(seed)
    // This shows time since last update, and can be used in methods
    var updateTime = 1.0 / updateRate

    val updateThread = TimedThread({ time -> this.updateTime = time; update(time) }, updateRate, useRealTime = mode == Mode.INTERACTIVE)
    val animatorThread = if (visualizer != null) TimedThread({ visualize() }, visualizeFrameRate, useRealTime = true) else null

    var onGameOver = { this.reset(); }

    /**
     * Runs the game synchronously - this function will not exit until the game finishes.
     */
    fun run() {
        init()
        updateThread.run()
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
    fun stop() {
        updateThread.stop()
        animatorThread?.stop()
        updateThread.join()
        animatorThread?.join()
    }

    fun reset() {
        levelGenerator.reset()
        playerController.reset()
        gameState = GameState(this, levelGenerator)
    }

    private fun init() {
        visualizer?.start()
        animatorThread?.start()
    }

    private fun visualize() {
        visualizer?.update(gameState)
    }

    private fun update(time: Double) {
        gameState.advance(time, true)

        val gameAction = playerController.onUpdate(gameState)
        if (gameAction?.isApplicableOn(gameState) == true)
            gameAction!!.applyOn(gameState)
    }
}