package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.MovingObject
import cz.woitee.endlessRunners.game.objects.Player
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

/**
 * An effect that changes the current speed of the player.
 */
data class SpeedChange(
    override val target: Target = Target.PLAYER,
    var targetSpeed: Double = 16.0,
    var relativity: Relativity = Relativity.ABSOLUTE
) : UndoableGameEffect() {

    override val timing = Timing.ONCE
    override val oppositeEffect: GameEffect
        get() = SpeedChange(target, -targetSpeed, relativity)

    override fun applyOn(gameState: GameState) {
        val target = (findTarget(gameState) as MovingObject?) ?: return
        val originalXSpeed = target.xspeed
        val desiredXSpeed = if (relativity == Relativity.ABSOLUTE) targetSpeed else target.xspeed + targetSpeed
        if (originalXSpeed == desiredXSpeed) return

        target.xspeed = desiredXSpeed
        (target as? Player)?.assertXSpeed()
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val target = (findTarget(gameState) as MovingObject?) ?: return NoUndo
        val originalXSpeed = target.xspeed
        val desiredXSpeed = if (relativity == Relativity.ABSOLUTE) targetSpeed else target.xspeed + targetSpeed
        if (originalXSpeed == desiredXSpeed) return NoUndo

        target.xspeed = desiredXSpeed
        (target as? Player)?.assertXSpeed()

        return object : IUndo {
            override fun undo(gameState: GameState) {
                target.xspeed = originalXSpeed
            }
        }
    }
}
