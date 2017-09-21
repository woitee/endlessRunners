package game.gameActions.composite

import game.GameState
import game.gameActions.abstract.UndoableAction
import game.gameConditions.GameCondition
import game.undoing.IUndo

class ConditionalAction(val condition: GameCondition, val action:UndoableAction): UndoableAction() {
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