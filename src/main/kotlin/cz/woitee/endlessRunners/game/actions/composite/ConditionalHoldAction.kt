package cz.woitee.endlessRunners.game.actions.composite

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.conditions.GameCondition
import cz.woitee.endlessRunners.game.conditions.TrueCondition
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A HoldAction that can be applied only if specific conditions are met.
 *
 * @param holdButtonAction The conditioned HoldAction
 * @param applicableCondition The condition that determines whether the hold action can be applied in a GameState
 * @param keptApplyingCondition The condition that determines whether the hold action can be kept applying in a GameState
 * @param stopApplyingCondition The condition that determines whether the hold action can be stopped applying in a GameState
 */
class ConditionalHoldAction(
    val holdButtonAction: HoldButtonAction,
    var applicableCondition: GameCondition,
    var keptApplyingCondition: GameCondition = TrueCondition(),
    var stopApplyingCondition: GameCondition = TrueCondition()
) : HoldButtonAction() {

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
