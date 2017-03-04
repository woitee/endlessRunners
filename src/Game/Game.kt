package Game

import Utils.TimedThread
import Game.PCG.ILevelGenerator
import Game.PlayerControllers.IPlayerController
import Game.Collisions.CollisionHandler

/**
 * Created by woitee on 13/01/2017.
 */

class Game(val levelGenerator: ILevelGenerator, val playerController: IPlayerController, val visualizer: IGameVisualizer?,
           val visualizeFrameRate: Double = 75.0, val updateRate: Double = 75.0, val mode: Mode = Mode.INTERACTIVE) {

    enum class Mode {
        INTERACTIVE, SIMULATION
    }

    val gameDescription = GameDescription()
    val collHandler = CollisionHandler(this)
    val gameState = GameState(this, levelGenerator)
    // This shows time since last update, and can be used in methods
    var updateTime = (1000/updateRate).toLong()

    val updateThread = TimedThread({ time -> this.updateTime = time; update(time) }, updateRate, useRealTime = mode == Mode.INTERACTIVE)
    val animatorThread = if (visualizer != null) TimedThread({ visualize() }, visualizeFrameRate) else null

    /**
     * Starts an asynchronous run of the game.
     */
    fun start() {
        init()
        updateThread.start()
    }

    /**
     * Runs the game synchronously - this function will not exit until the game finishes.
     */
    fun run() {
        init()
        updateThread.run()
        // done
        stop()
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

    private fun init() {
        visualizer?.start()
        animatorThread?.start()
    }

    private fun visualize() {
        visualizer?.update(gameState)
    }

    private fun update(time: Long) {
        gameState.advance(time, true)

        val gameAction = playerController.onUpdate(gameState)
        if (gameAction?.isPerformableOn(gameState) == true)
            gameAction!!.performOn(gameState)
    }
}