package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.objects.GameObject
import java.io.Serializable

/**
 * A collision effect represents anything that can happen when two objects collide.
 */

interface ICollisionEffect : Serializable {
    /**
     * Apply this effect in the GameState of the source, depending on the occuring Collision.
     */
    fun apply(source: GameObject, collision: Collision)
}
