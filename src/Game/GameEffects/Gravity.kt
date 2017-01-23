package Game.GameEffects

import Game.GameObjects.MovingObject
import Game.GameState
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
        var strength: Double = 0.01): GameEffect() {
    override val target = targetedAt
    override val timing = GameEffect.Timing.PERSISTENT

    override fun apply(gameState: GameState) {
        val target = (findTarget(gameState) as MovingObject?) ?: return

        target.yspeed -= strength
    }
}