package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.MovingObject
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

class SpeedChange (
    targetedAt: Target = Target.PLAYER,
    var targetSpeed: Double = 16.0,
    var relativity: Relativity = Relativity.ABSOLUTE): UndoableGameEffect() {

    override val target = targetedAt
    override val timing = Timing.ONCE

    override fun applyOn(gameState: GameState) {
        val target = (findTarget(gameState) as MovingObject?) ?: return
        val originalXSpeed = target.xspeed
        val desiredXSpeed = if (relativity == Relativity.ABSOLUTE) targetSpeed else target.xspeed + targetSpeed
        if (originalXSpeed == desiredXSpeed) return

        target.xspeed = desiredXSpeed
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        val target = (findTarget(gameState) as MovingObject?) ?: return NoUndo
        val originalXSpeed = target.xspeed
        val desiredXSpeed = if (relativity == Relativity.ABSOLUTE) targetSpeed else target.xspeed + targetSpeed
        if (originalXSpeed == desiredXSpeed) return NoUndo

        target.xspeed = desiredXSpeed

        return object : IUndo {
            override fun undo(gameState: GameState) {
                target.xspeed = originalXSpeed
            }
        }
    }
}