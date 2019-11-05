package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A CollisionEffect that can be undone.
 */

interface IUndoableCollisionEffect : ICollisionEffect {
    fun applyUndoable(source: GameObject, collision: Collision): IUndo
}
