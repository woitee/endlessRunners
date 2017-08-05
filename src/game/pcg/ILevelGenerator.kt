package game.pcg

import game.gameObjects.GameObject
import game.GameState
import java.util.*

/**
 * Created by woitee on 14/01/2017.
 */

interface ILevelGenerator {
    fun generateNextColumn(gameState: GameState): ArrayList<GameObject?>
    fun reset()
}