package Game.GameActions

import Game.GameState
import Game.BlockHeight
import Game.BlockWidth
import Game.GameObjects.SolidBlock
import Game.Undoing.IUndo

/**
 * Created by woitee on 13/01/2017.
 */

class JumpAction(val power:Double): UndoableAction() {
    class JumpActionUndo(val originalPlayerYSpeed: Double): IUndo {
        override fun undo(gameState: GameState) {
            gameState.player.yspeed = this.originalPlayerYSpeed
        }
    }
    var oldSpeed = 0.0

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
        val undo = JumpActionUndo(gameState.player.yspeed)
        gameState.player.yspeed = power
        return undo
    }
}