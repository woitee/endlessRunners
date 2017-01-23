package Game.Collisions.CollisionEffects

import Game.Collisions.Collision
import Game.GameEffects.GameEffect
import Game.GameObjects.GameObject
import Game.GameState

/**
 * Created by woitee on 23/01/2017.
 */

class ApplyGameEffect(val gameEffect: GameEffect): CollisionEffect() {
    override fun apply(source: GameObject, collision: Collision) {
        gameEffect.apply(source.gameState)
    }
}