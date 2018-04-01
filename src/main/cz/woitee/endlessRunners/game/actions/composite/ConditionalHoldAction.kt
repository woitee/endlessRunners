package cz.woitee.game.actions.composite

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.HoldButtonAction
import cz.woitee.game.conditions.GameCondition
import cz.woitee.game.conditions.TrueCondition
import cz.woitee.game.undoing.IUndo

class ConditionalHoldAction(
        val holdButtonAction: HoldButtonAction,
        val applicableCondition: GameCondition,
        val keptApplyingCondition: GameCondition = TrueCondition(),
        val stopApplyingCondition: GameCondition = TrueCondition()): HoldButtonAction() {

    override fun isApplicableOn(gameState: GameState): Boolean {
        return applicableCondition.isTrue(gameState)
    }

    override fun canBeKeptApplyingOn(gameState: GameState): Boolean {
        return keptApplyingCondition.isTrue(gameState)
    }

    override fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        return stopApplyingCondition.isTrue(gameState)
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        return holdButtonAction.applyUndoablyOn(gameState)
    }

    override fun stopApplyingUndoablyOn(gameState: GameState): IUndo {
        return holdButtonAction.stopApplyingUndoablyOn(gameState)
    }

    override fun applyOn(gameState: GameState) {
        return holdButtonAction.applyOn(gameState)
    }

    override fun stopApplyingOn(gameState: GameState) {
        return holdButtonAction.stopApplyingOn(gameState)
    }
}