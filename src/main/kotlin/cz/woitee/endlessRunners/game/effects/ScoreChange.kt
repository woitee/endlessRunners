package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo

class ScoreChange(val amount: Int) : UndoableGameEffect() {
    override fun applyOn(gameState: GameState) {
        gameState.score += amount
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        gameState.score += amount
        return object: IUndo {
            override fun undo(gameState: GameState) {
                gameState.score -= amount
            }
        }
    }
}