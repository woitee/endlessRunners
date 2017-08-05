package game

import java.awt.event.KeyListener

/**
 * Created by woitee on 13/01/2017.
 */

interface IGameVisualizer {
    fun start()
    fun update(gameState: GameState)
    fun stop()
    fun addKeyListener(listener: KeyListener)
}
