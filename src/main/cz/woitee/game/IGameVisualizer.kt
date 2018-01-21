package cz.woitee.game

import java.awt.event.KeyListener

/**
 * Created by woitee on 13/01/2017.
 */

interface IGameVisualizer {
    fun init()
    fun update(gameState: GameState)
    fun dispose()
    fun addKeyListener(listener: KeyListener)
}
