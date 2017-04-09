package Game.Collisions.CollisionEffects

import Game.Collisions.Collision
import Game.GameObjects.GameObject
import Game.GameState

/**
 * Created by woitee on 23/01/2017.
 */

interface ICollisionEffect {
    fun apply(source: GameObject, collision: Collision)
}