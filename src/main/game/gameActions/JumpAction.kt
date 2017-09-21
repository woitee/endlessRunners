package game.gameActions

import game.GameState
import game.BlockHeight
import game.BlockWidth
import game.gameActions.abstract.UndoableAction
import game.gameObjects.SolidBlock
import game.undoing.IUndo

/**
 * Created by woitee on 13/01/2017.
 */

class JumpAction(val power:Double): UndoableAction() {
    override fun isApplicableOn(gameState: GameState): Boolean {
        val x = gameState.player.x
        val y = gameState.player.y - 1
        val gridX = (x / BlockWidth).toInt() - gameState.gridX
        val gridY = (y / BlockHeight).toInt()

        return gameState.grid[gridX, gridY] is SolidBlock
    }

    override fun applyOn(gameState: GameState) {
        gameState.player.yspeed = power
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val originalYSpeed = gameState.player.yspeed
        applyOn(gameState)

        return object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.player.yspeed = originalYSpeed
            }
        }
    }
}