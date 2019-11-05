package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoableCollisionEffect : ICollisionEffect {
    fun applyUndoable(source: GameObject, collision: Collision): IUndo
}