package game.collisions.collisionEffects

import game.collisions.Collision
import game.objects.GameObject
import game.undoing.IUndo

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoableCollisionEffect : ICollisionEffect {
    fun applyUndoable(source: GameObject, collision: Collision): IUndo
}