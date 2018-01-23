package cz.woitee.game.actions.abstract

import cz.woitee.game.GameState
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.NoUndo

/**
 * A class facilitating an gameAction that can be held for a duration of time.
 * Except for being an interface, it facilitates some of the mandatory things for a hold gameAction,
 * e.g. logging the time init into GameState's heldActions variable.
 *
 * You can extract non-hold Actions from it by getting asStartAction, and asStopAction.
 *
 * Created by woitee on 23/07/2017.
 */
abstract class HoldButtonAction : GameButtonAction() {
    fun isAppliedIn(gameState: GameState): Boolean {
        return gameState.heldActions.containsKey(this)
    }
    fun wasAppliedAt(gameState: GameState): Double? {
        return gameState.heldActions[this]
    }

    abstract fun stopApplyingOn(gameState: GameState)
    abstract fun stopApplyingUndoablyOn(gameState: GameState): IUndo

    // The following are non-mandatory functions that allow the derived class to have more control over the design
    // They are called only if the method is already held, so no need to check for that
    open fun keepApplyingOn(gameState: GameState) {
    }
    open fun keepApplyingUndoablyOn(gameState: GameState): IUndo {
        return NoUndo
    }
    // Friendly note - these methods shouldn't both return false in the same GameState
    open fun canBeKeptApplyingOn(gameState: GameState): Boolean {
        return true
    }
    open fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        return true
    }
}