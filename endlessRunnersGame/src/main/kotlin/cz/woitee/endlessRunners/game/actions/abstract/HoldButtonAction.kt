package cz.woitee.endlessRunners.game.actions.abstract

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo
import java.io.Serializable

/**
 * A class facilitating an gameAction that can be held for a duration of time.
 * Except for being an interface, it facilitates some of the mandatory things for a hold gameAction,
 * e.g. logging the time init into GameState's heldActions variable.
 *
 * You can extract non-hold Actions from it by getting asStartAction, and asStopAction.
 */
abstract class HoldButtonAction : GameAction(), Serializable {
    // This determines whether this action should be controlled as an on/off switch
    open val isToggleControlled = false

    /**
     * Returns whether the player is currently performing the action in a GameState.
     */
    fun isAppliedIn(gameState: GameState): Boolean {
        return gameState.heldActions.containsKey(this)
    }
    /**
     * Returns the time that this action was started being held in a GameState.
     */
    fun wasAppliedAt(gameState: GameState): Double? {
        return gameState.heldActions[this]
    }

    /**
     * Perform the stop applying (release) effect of  the action in the GameState.
     */
    abstract fun stopApplyingOn(gameState: GameState)

    /**
     * Perform the stop applying (release) action in the GameState, and return an IUndo object.
     */
    abstract fun stopApplyingUndoablyOn(gameState: GameState): IUndo

    // The following are non-mandatory functions that allow the derived class to have more control over the design
    // They are called only if the method is already held, so no need to check for that
    /**
     * Perform effects when this action is kept applying in a given GameState.
     */
    open fun keepApplyingOn(gameState: GameState) {
    }

    /**
     * Perform effects when this action is kept applying in a given GameState, and return an IUndo object.
     */
    open fun keepApplyingUndoablyOn(gameState: GameState): IUndo {
        return NoUndo
    }

    // Friendly note - these methods shouldn't both return false in the same GameState
    /**
     * Checks whether we can continue performing the action in a GameState.
     */
    open fun canBeKeptApplyingOn(gameState: GameState): Boolean {
        return true
    }

    /**
     * Checks whether we can stop performing the action in a GameState.
     */
    open fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        return true
    }
}
