package cz.woitee.endlessRunners.game.actions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * An action of jump that is allowed to double- or multi-jump in general.
 *
 * The attributes are the target yspeed when using jump and the number of jumps allowed in the air.
 * You can pass a negative value to the second attribute for infinite double jumps.
 */
class MultiJumpAction(power: Double, val multiplier: Int = 1) : JumpAction(power) {
    override val onlyOnPress = true

    override fun isApplicableOn(gameState: GameState): Boolean {
        if (gameState.player.timesJumpedSinceTouchingGround < multiplier) {
            return true
        }
        return super.isApplicableOn(gameState)
    }

    override fun applyOn(gameState: GameState) {
        if (!isPlayerTouchingGround(gameState)) {
            gameState.player.timesJumpedSinceTouchingGround += 1
        }
        super.applyOn(gameState)
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val originalYSpeed = gameState.player.yspeed
        val originalTimesJumped = gameState.player.timesJumpedSinceTouchingGround
        applyOn(gameState)

        return object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.player.yspeed = originalYSpeed
                gameState.player.timesJumpedSinceTouchingGround = originalTimesJumped
            }
        }
    }

    override fun toString(): String {
        return "MultiJumpAction(power=$power, multiplier=$multiplier)"
    }
}
