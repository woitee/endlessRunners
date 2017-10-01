package cz.woitee.game.levelGenerators

import cz.woitee.game.objects.GameObject
import cz.woitee.game.GameState
import java.util.*

/**
 * Created by woitee on 14/01/2017.
 */

abstract class LevelGenerator{
    abstract fun generateNextColumn(gameState: GameState): ArrayList<GameObject?>
    abstract fun reset()
}