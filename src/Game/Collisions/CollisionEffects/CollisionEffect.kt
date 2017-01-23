package Game.Collisions.CollisionEffects

import Game.Collisions.Collision
import Game.GameObjects.GameObject
import Game.GameState

/**
 * Created by woitee on 23/01/2017.
 */

abstract class CollisionEffect {
    abstract fun apply(source: GameObject, collision: Collision)
}