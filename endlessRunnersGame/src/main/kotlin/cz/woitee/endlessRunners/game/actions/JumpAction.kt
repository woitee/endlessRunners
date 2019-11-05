package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.BlockWidth
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * The most common action in endless runners - the jump action.
 *
 * @param power Power of the jump, as the immediate vertical speed after jumping.
 */

open class JumpAction(val power: Double) : GameAction() {
    override val onlyOnPress = false

    companion object {
        fun isPlayerTouchingGround(gameState: GameState): Boolean {
            val x = gameState.player.x
            val y = gameState.player.y - 1
            val gridX = (x / BlockWidth).toInt() - gameState.gridX
            val gridY = (y / BlockHeight).toInt()

            return gameState.grid.contains(gridX, gridY) && gameState.grid[gridX, gridY]?.isSolid == true
        }
    }

    override fun isApplicableOn(gameState: GameState): Boolean {
        return isPlayerTouchingGround(gameState)
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

    override fun toString(): String {
        return "JumpAction(power=$power)"
    }
}
