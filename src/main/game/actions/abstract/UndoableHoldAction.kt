package game.actions.abstract

import game.GameState
import game.undoing.IUndo
import game.undoing.IUndoable

/**
 * An abstract class to facilitate different representations of a hold action. A hold action can be represented
 * also as two regular actions, one handling the "press" and the other the "release" of the action.
 *
 * Created by woitee on 23/07/2017.
 */
abstract class UndoableHoldAction(minimumHoldTime: Double) : HoldAction(minimumHoldTime), IUndoable {
    class AsStartUndoableAction(val undoableHoldAction: UndoableHoldAction): UndoableAction() {
        override fun applyOn(gameState: GameState) {
            return undoableHoldAction.applyOn(gameState)
        }
        override fun isApplicableOn(gameState: GameState): Boolean {
            return undoableHoldAction.isApplicableOn(gameState)
        }
        override fun applyUndoablyOn(gameState: GameState): IUndo {
            return undoableHoldAction.applyUndoablyOn(gameState)
        }
    }
    class AsStopUndoableAction(val undoableHoldAction: UndoableHoldAction): UndoableAction() {
        override fun applyOn(gameState: GameState) {
            return undoableHoldAction.stopApplyingOn(gameState)
        }
        override fun isApplicableOn(gameState: GameState): Boolean {
            return undoableHoldAction.canBeStoppedApplyingOn(gameState)
        }
        override fun applyUndoablyOn(gameState: GameState): IUndo {
            return undoableHoldAction.stopApplyingUndoablyOn(gameState)
        }
    }
    override val asStartAction: UndoableAction = AsStartUndoableAction(this)
    override val asStopAction: UndoableAction = AsStopUndoableAction(this)

    /**
     * Class that does the inner workings when undoing a HoldAction, that is,
     * like removing it from GameState's heldActions.
     */
    abstract class HoldActionUndo(val holdAction: UndoableHoldAction): IUndo {
        override final fun undo(gameState: GameState) {
            innerUndo(gameState)
            gameState.heldActions.remove(holdAction)
        }
        abstract fun innerUndo(gameState: GameState)
    }

    /**
     * Class that does the inner workings when undoing the release of a HoldAction,
     * like adding it back to GameState's heldActions.
     */
    abstract class HoldActionStopUndo(val holdAction: UndoableHoldAction, val startTime: Double): IUndo {
        override final fun undo(gameState: GameState) {
            innerUndo(gameState)
            gameState.heldActions[holdAction] = startTime
        }
        abstract fun innerUndo(gameState: GameState)
    }

    override final fun applyUndoablyOn(gameState: GameState): HoldActionUndo {
        val res = innerApplyUndoablyOn(gameState)
        // TODO: rewrite this so it isn't a copy of code from HoldAction
        gameState.heldActions[this] = gameState.gameTime
        return res
    }
    fun stopApplyingUndoablyOn(gameState: GameState): HoldActionStopUndo {
        val res = innerStopApplyingUndoablyOn(gameState, gameState.heldActions[this]!!)
        // TODO: rewrite this so it isn't a copy of code from HoldAction
        gameState.heldActions.remove(this)
        return res
    }

    abstract fun innerApplyUndoablyOn(gameState: GameState): HoldActionUndo
    abstract fun innerStopApplyingUndoablyOn(gameState: GameState, timeStart: Double): HoldActionStopUndo
}