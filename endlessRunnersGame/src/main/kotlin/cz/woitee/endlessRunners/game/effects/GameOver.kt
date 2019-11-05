package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * An effect that causes GameOver - losing the game.
 */

class GameOver : UndoableGameEffect() {
    class GameOverUndo : IUndo {
        override fun undo(gameState: GameState) {
            gameState.isGameOver = false
        }
    }

    override fun applyOn(gameState: GameState) {
        gameState.isGameOver = true
//        println("-------------")
//        println("| GAME OVER |")
//        println("-------------")
//        println(LocalDateTime.now())
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        gameState.isGameOver = true
        return GameOverUndo()
    }

    override fun toString(): String {
        return "GameOver"
    }
}
