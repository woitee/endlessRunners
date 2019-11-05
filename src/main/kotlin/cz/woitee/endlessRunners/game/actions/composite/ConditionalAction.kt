package cz.woitee.endlessRunners.game.actions.composite

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.NoAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.conditions.GameCondition
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A GameAction that can have two different outcomes based on a condition.
 *
 * @param condition The condition that this action is based on
 * @param trueAction The action that will occur when the condition is true
 * @param falseAction The action that will occur when the condition is false
 */
class ConditionalAction(var condition: GameCondition, val trueAction: GameAction, val falseAction: GameAction = NoAction) : GameAction() {
    override fun isApplicableOn(gameState: GameState): Boolean {
        return if (condition.isTrue(gameState)) trueAction.isApplicableOn(gameState) else falseAction.isApplicableOn(gameState)
    }

    override fun applyOn(gameState: GameState) {
        if (condition.isTrue(gameState)) trueAction.applyOn(gameState) else falseAction.applyOn(gameState)
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val action = if (condition.isTrue(gameState)) trueAction else falseAction
        return action.applyUndoablyOn(gameState)
    }

    override fun toString(): String {
        return "ConditionalAction($condition, $trueAction, $falseAction)"
    }
}
