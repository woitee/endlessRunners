package Game

/**
 * Created by woitee on 13/01/2017.
 */

interface IGameVisualizer {
    fun start()
    fun update(gameState: GameState)
    fun stop()
}
