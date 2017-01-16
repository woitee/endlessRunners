package Game.PCG

import Game.GameObjects.GameObject
import Game.GameState
import java.util.*

/**
 * Created by woitee on 14/01/2017.
 */

interface ILevelGenerator {
    fun generateNextColumn(gameState: GameState): ArrayList<GameObject?>
}