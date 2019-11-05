package cz.woitee.endlessRunners.game

import java.awt.event.KeyListener

/**
 * An underlying abstraction of game visualization.
 */

abstract class GameVisualizerBase {
    abstract fun init()
    abstract fun update(gameState: GameState)
    abstract fun dispose()
    fun stopGame(gameState: GameState) {
        gameState.game.updateThread.stop()
        gameState.game.endedFromVisualizer = true
    }
    abstract fun addKeyListener(listener: KeyListener)
}
