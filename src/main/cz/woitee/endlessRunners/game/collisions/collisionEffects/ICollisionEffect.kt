package cz.woitee.game.collisions.collisionEffects

import cz.woitee.game.collisions.Collision
import cz.woitee.game.objects.GameObject

/**
 * Created by woitee on 23/01/2017.
 */

interface ICollisionEffect {
    fun apply(source: GameObject, collision: Collision)
}