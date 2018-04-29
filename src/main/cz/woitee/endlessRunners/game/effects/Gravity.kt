package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.objects.MovingObject
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

/**
 * Gravity effect, defaultly targeted on player, but can also be used for other objects.
 * It also states that the objects collide between each other.
 *
 * Created by woitee on 16/01/2017.
 */

class Gravity(
        targetedAt: Target = Target.PLAYER,
        var strength: Double = 0.5): UndoableGameEffect() {

    override val target = targetedAt
    override val timing = Timing.PERSISTENT

    override fun applyOn(gameState: GameState) {
        val target = (findTarget(gameState) as MovingObject?) ?: return
        if (gameState.atLocation(target.x + target.widthPx / 2, target.y - 1)?.isSolid ?: false) {
            return
        }

        target.yspeed -= strength * BlockHeight * gameState.game.updateTime
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        // Don't fall if a block is underneath
        val target = (findTarget(gameState) as MovingObject?) ?: return NoUndo
        if (gameState.atLocation(target.x + target.widthPx / 2, target.y - 1)?.isSolid ?: false) {
            return NoUndo
        }

        // Else fall
        val originalYSpeed = target.yspeed
        target.yspeed -= strength * BlockHeight * gameState.game.updateTime

        return object : IUndo {
            override fun undo(gameState: GameState) {
                val targetInGameState = findTarget(gameState)
                if (targetInGameState != null)
                    target.yspeed = originalYSpeed
            }
        }
    }
}