package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.effects.GameOver
import cz.woitee.endlessRunners.game.undoing.IUndo

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