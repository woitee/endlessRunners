package cz.woitee.game.actions.composite

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.conditions.GameCondition
import cz.woitee.game.undoing.IUndo

class ConditionalAction(val condition: GameCondition, val action: GameAction): GameAction() {
    override fun applyOn(gameState: GameState) {
        action.applyOn(gameState)
    }

    override fun isApplicableOn(gameState: GameState): Boolean {
        return condition.isTrue(gameState)
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        return action.applyUndoablyOn(gameState)
    }
}