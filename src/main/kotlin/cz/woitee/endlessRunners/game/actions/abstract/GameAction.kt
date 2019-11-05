package cz.woitee.endlessRunners.game.actions.abstract

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndoable
import java.io.Serializable

/**
 * An object describing an action that can be performed by the player.
 */

abstract class GameAction : IUndoable, Serializable {
    open val onlyOnPress = false

    /**
     * Checks whether the action is performable in a given GameState.
     */
    abstract fun isApplicableOn(gameState: GameState): Boolean

    /**
     * Performs the action in a given GameState.
     */
    abstract fun applyOn(gameState: GameState)
}
