package cz.woitee.game.actions.abstract

import cz.woitee.game.GameState
import cz.woitee.game.undoing.IUndo

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
    abstract fun stopApplyingOn(gameState: GameState)
    abstract fun stopApplyingUndoablyOn(gameState: GameState): IUndo

    // The following are non-mandatory functions that allow the derived class to have more control over the design
    open fun canBeKeptApplyingOn(gameState: GameState): Boolean {
        return true
    }
    open fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        return true
    }
}