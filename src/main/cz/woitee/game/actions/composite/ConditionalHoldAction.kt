package cz.woitee.game.actions.composite

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.UndoableHoldAction
import cz.woitee.game.conditions.GameCondition
import cz.woitee.game.conditions.TrueCondition

class ConditionalHoldAction(
        val holdAction: UndoableHoldAction,
        val applicableCondition: GameCondition,
        val keptApplyingCondition: GameCondition = TrueCondition(),
        val stopApplyingCondition: GameCondition = TrueCondition()): UndoableHoldAction(holdAction.minimumHoldTime) {

    override fun innerIsApplicableOn(gameState: GameState): Boolean {
        return applicableCondition.isTrue(gameState)
    }

    override fun innerCanBeKeptApplyingOn(gameState: GameState): Boolean {
        return keptApplyingCondition.isTrue(gameState)
    }

    override fun innerCanBeStoppedApplyingOn(gameState: GameState): Boolean {
        return stopApplyingCondition.isTrue(gameState)
    }

    override fun innerApplyUndoablyOn(gameState: GameState): HoldActionUndo {
        return holdAction.applyUndoablyOn(gameState)
    }

    override fun innerStopApplyingUndoablyOn(gameState: GameState, timeStart: Double): HoldActionStopUndo {
        return holdAction.innerStopApplyingUndoablyOn(gameState, timeStart)
    }

    override fun innerApplyOn(gameState: GameState) {
        return holdAction.innerApplyOn(gameState)
    }

    override fun innerStopApplyingOn(gameState: GameState, timeStart: Double) {
        return holdAction.innerStopApplyingOn(gameState, timeStart)
    }
}