package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo
import java.time.LocalDateTime

/**
 * Created by woitee on 23/01/2017.
 */

class GameOver : UndoableGameEffect() {
    class GameOverUndo : IUndo {
        override fun undo(gameState: GameState) {
            gameState.isGameOver = false
        }
    }

    override fun applyOn(gameState: GameState) {
        gameState.isGameOver = true
        println("-------------")
        println("| GAME OVER |")
        println("-------------")
        println(LocalDateTime.now())
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        gameState.isGameOver = true
        return GameOverUndo()
    }
}
