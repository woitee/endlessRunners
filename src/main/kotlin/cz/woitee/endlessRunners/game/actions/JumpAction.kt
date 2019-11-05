package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.BlockWidth
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * Created by woitee on 13/01/2017.
 */

class JumpAction(val power:Double): GameButtonAction() {
    override fun isApplicableOn(gameState: GameState): Boolean {
        val x = gameState.player.x
        val y = gameState.player.y - 1
        val gridX = (x / BlockWidth).toInt() - gameState.gridX
        val gridY = (y / BlockHeight).toInt()

        return gameState.grid[gridX, gridY] is SolidBlock
    }

    override fun applyOn(gameState: GameState) {
        val debugTime = if (gameState.tag != "delayed") gameState.gameTime else gameState.gameTime + 0.24000000000003752
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