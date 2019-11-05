package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState
import java.io.Serializable

/**
 * A condition that can be either true or false in a GameState.
 */
abstract class GameCondition : Serializable {
    abstract fun isTrue(gameState: GameState): Boolean
}
