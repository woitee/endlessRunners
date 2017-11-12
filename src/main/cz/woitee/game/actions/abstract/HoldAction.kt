package cz.woitee.game.actions.abstract

import cz.woitee.game.GameState
import cz.woitee.game.undoing.IUndo

/**
 * A class facilitating an action that can be held for a duration of time.
 * Except for being an interface, it facilitates some of the mandatory things for a hold action,
 * e.g. logging the time start into GameState's heldActions variable.
 *
 * You can extract non-hold Actions from it by getting asStartAction, and asStopAction.
 *
 * Created by woitee on 23/07/2017.
 */
abstract class HoldAction(val minimumHoldTime: Double) : HalfOfHoldAction() {
    class AsStartUndoableAction(val holdAction: HoldAction): HalfOfHoldAction() {
        override val opposite: HalfOfHoldAction
            get() = holdAction.asStopAction
        override fun applyOn(gameState: GameState) {
            return holdAction.applyOn(gameState)
        }
        override fun isApplicableOn(gameState: GameState): Boolean {
            return holdAction.isApplicableOn(gameState)
        }
        override fun applyUndoablyOn(gameState: GameState): IUndo {
            return holdAction.applyUndoablyOn(gameState)
        }
    }
    class AsStopUndoableAction(val holdAction: HoldAction): HalfOfHoldAction() {
        override val opposite: HalfOfHoldAction
            get() = holdAction.asStartAction
        override fun applyOn(gameState: GameState) {
            return holdAction.stopApplyingOn(gameState)
        }
        override fun isApplicableOn(gameState: GameState): Boolean {
            return holdAction.canBeStoppedApplyingOn(gameState)
        }
        override fun applyUndoablyOn(gameState: GameState): IUndo {
            return holdAction.stopApplyingUndoablyOn(gameState)
        }
    }

    val asStopAction: HalfOfHoldAction = AsStopUndoableAction(this)
    val asStartAction: HalfOfHoldAction = AsStartUndoableAction(this)
    override val opposite: HalfOfHoldAction
        get() = asStopAction

    override final fun isApplicableOn(gameState: GameState): Boolean {
        return !gameState.heldActions.containsKey(this) && this.innerIsApplicableOn(gameState)
    }
    override final fun applyOn(gameState: GameState) {
        innerApplyOn(gameState)
        gameState.heldActions[this] = gameState.gameTime
    }
    fun stopApplyingOn(gameState: GameState) {
        val heldActionTime = gameState.heldActions[this]!!
        gameState.heldActions.remove(this)
        innerStopApplyingOn(gameState, heldActionTime)
    }
    fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        if (!gameState.heldActions.containsKey(this))
            return false

        val heldTime = gameState.gameTime - gameState.heldActions[this]!!
        return heldTime >= minimumHoldTime && this.innerCanBeStoppedApplyingOn(gameState)
    }
    fun canBeKeptApplyingOn(gameState: GameState): Boolean {
        return gameState.heldActions.containsKey(this) && this.innerCanBeKeptApplyingOn(gameState)
    }

    // Undoing

    /** Class that does the inner workings when undoing a HoldAction, that is,
    * like removing it from GameState's heldActions.
    */
    abstract class HoldActionUndo(val holdAction: HoldAction): IUndo {
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
    abstract class HoldActionStopUndo(val holdAction: HoldAction, val startTime: Double): IUndo {
        override final fun undo(gameState: GameState) {
            innerUndo(gameState)
            gameState.heldActions[holdAction] = startTime
        }
        abstract fun innerUndo(gameState: GameState)
    }

    override final fun applyUndoablyOn(gameState: GameState): HoldActionUndo {
        val res = innerApplyUndoablyOn(gameState)
        gameState.heldActions[this] = gameState.gameTime
        return res
    }
    fun stopApplyingUndoablyOn(gameState: GameState): HoldActionStopUndo {
        val heldActionTime = gameState.heldActions[this]!!
        gameState.heldActions.remove(this)
        val res = innerStopApplyingUndoablyOn(gameState, heldActionTime)
        return res
    }

    abstract internal fun innerIsApplicableOn(gameState: GameState): Boolean
    abstract internal fun innerCanBeKeptApplyingOn(gameState: GameState): Boolean
    abstract internal fun innerCanBeStoppedApplyingOn(gameState: GameState): Boolean
    abstract internal fun innerApplyOn(gameState: GameState)
    abstract internal fun innerStopApplyingOn(gameState: GameState, timeStart: Double)
    abstract internal fun innerApplyUndoablyOn(gameState: GameState): HoldActionUndo
    abstract internal fun innerStopApplyingUndoablyOn(gameState: GameState, timeStart: Double): HoldActionStopUndo
}