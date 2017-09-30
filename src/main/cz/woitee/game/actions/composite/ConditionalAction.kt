package cz.woitee.game.actions.composite

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.UndoableAction
import cz.woitee.game.conditions.GameCondition
import cz.woitee.game.undoing.IUndo

class ConditionalAction(val condition: GameCondition, val action: UndoableAction): UndoableAction() {
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