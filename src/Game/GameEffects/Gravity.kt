package Game.GameEffects

import Game.GameObjects.MovingObject
import Game.GameState
import Game.Undoing.IUndo
import Geom.Vector2Double
import Geom.Vector2Int

/**
 * Gravity effect, defaultly targeted on player, but can also be used for other objects.
 * It also states that the objects collide between each other.
 *
 * Created by woitee on 16/01/2017.
 */

class Gravity(
        targetedAt: GameEffect.Target = GameEffect.Target.PLAYER,
        var strength: Double = 0.5): UndoableGameEffect() {

    class GravityUndo(val gravity: Gravity): IUndo {
        override fun undo(gameState: GameState) {
            val target = (gravity.findTarget(gameState) as MovingObject?) ?: return
            target.yspeed += gravity.strength
        }
    }

    override val target = targetedAt
    override val timing = GameEffect.Timing.PERSISTENT

    override fun applyOn(gameState: GameState) {
        val target = (findTarget(gameState) as MovingObject?) ?: return

        target.yspeed -= strength
    }

    override fun applyUndoableOn(gameState: GameState): IUndo {
        applyOn(gameState)
        return GravityUndo(this)
    }
}