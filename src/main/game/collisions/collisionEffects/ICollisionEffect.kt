package game.collisions.collisionEffects

import game.collisions.Collision
import game.objects.GameObject

/**
 * Created by woitee on 23/01/2017.
 */

interface ICollisionEffect {
    fun apply(source: GameObject, collision: Collision)
}