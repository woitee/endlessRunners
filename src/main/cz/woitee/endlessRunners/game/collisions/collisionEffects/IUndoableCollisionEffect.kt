package cz.woitee.game.collisions.collisionEffects

import cz.woitee.game.collisions.Collision
import cz.woitee.game.objects.GameObject
import cz.woitee.game.undoing.IUndo

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoableCollisionEffect : ICollisionEffect {
    fun applyUndoable(source: GameObject, collision: Collision): IUndo
}