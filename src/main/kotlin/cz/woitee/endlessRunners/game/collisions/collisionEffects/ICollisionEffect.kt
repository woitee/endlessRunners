package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.objects.GameObject

/**
 * Created by woitee on 23/01/2017.
 */

interface ICollisionEffect {
    fun apply(source: GameObject, collision: Collision)
}
