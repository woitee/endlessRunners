package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.MovingObject
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

/**
 * Gravity effect, defaultly targeted on player, but could also be used for other objects.
 * It also states that the objects collide between each other.
 */

data class Gravity(
    override val target: Target = Target.PLAYER,
    var strength: Double = 0.5
) : UndoableGameEffect() {

    override val timing = Timing.PERSISTENT
    override val oppositeEffect: GameEffect
        get() = Gravity(target, -strength)

    override fun applyOn(gameState: GameState) {
        val target = (findTarget(gameState) as MovingObject?) ?: return
//        if (strength > 0 && gameState.atLocation(target.x + target.widthPx / 2, target.y - 1)?.isSolid == true) {
//            return
//        }

        target.yspeed -= strength * BlockHeight * gameState.game.updateTime
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        // Don't fall if a block is underneath
        val target = (findTarget(gameState) as MovingObject?) ?: return NoUndo
//        if (gameState.atLocation(target.x + target.widthPx / 2, target.y - 1)?.isSolid == true) {
//            return NoUndo
//        }

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
