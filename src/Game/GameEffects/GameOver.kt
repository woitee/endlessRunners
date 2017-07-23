package Game.GameEffects

import Game.GameState
import Game.Undoing.IUndo

/**
 * Created by woitee on 23/01/2017.
 */

class GameOver: UndoableGameEffect() {
    class GameOverUndo: IUndo {
        override fun undo(gameState: GameState) {
            gameState.isGameOver = false
        }
    }

    override fun applyOn(gameState: GameState) {
        gameState.isGameOver = true
        println("-------------")
        println("| GAME OVER |")
        println("-------------")
        gameState.game.onGameOver()
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        gameState.isGameOver = true
        return GameOverUndo()
    }
}