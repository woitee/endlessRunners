package cz.woitee.game.actions

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameButtonAction
import cz.woitee.game.effects.GameOver
import cz.woitee.game.undoing.IUndo

class GameOverAction(): GameButtonAction() {
    val gameOver = GameOver()

    override fun isApplicableOn(gameState: GameState): Boolean {
        return true
    }

    override fun applyOn(gameState: GameState) {
        gameOver.applyOn(gameState)
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        return gameOver.applyUndoablyOn(gameState)
    }
}