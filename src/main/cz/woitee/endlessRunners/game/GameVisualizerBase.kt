package cz.woitee.endlessRunners.game

import java.awt.event.KeyListener

/**
 * Created by woitee on 13/01/2017.
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
