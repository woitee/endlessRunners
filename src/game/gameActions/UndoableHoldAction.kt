package game.gameActions

import game.GameState
import game.undoing.IUndo
import game.undoing.IUndoable

/**
 * Created by woitee on 23/07/2017.
 */
abstract class UndoableHoldAction : HoldAction(), IUndoable {
    /**
     * Class that does the inner workings when undoing a HoldAction, that is,
     * like removing it from GameState's heldActions.
     */
    abstract class HoldActionUndo(val holdAction: UndoableHoldAction): IUndo {
        override fun undo(gameState: GameState) {
            gameState.heldActions.remove(holdAction)
        }
        abstract fun innerUndo(gameState: GameState)
    }

    /**
     * Class that does the inner workings when undoing the release of a HoldAction,
     * like adding it back to GameState's heldActions.
     */
    abstract class HoldActionStopUndo(val holdAction: UndoableHoldAction, val startTime: Double): IUndo {
        override fun undo(gameState: GameState) {
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