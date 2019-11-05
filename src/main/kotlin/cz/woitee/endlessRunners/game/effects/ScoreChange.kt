package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A change of the score, can be positive (good) or negative (bad).
 */
data class ScoreChange(val amount: Int) : UndoableGameEffect() {
    override val oppositeEffect: GameEffect
        get() = ScoreChange(-amount)

    override fun applyOn(gameState: GameState) {
        gameState.score += amount
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        gameState.score += amount
        return object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.score -= amount
            }
        }
    }
}
