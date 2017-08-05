package game.collisions.collisionEffects

import game.collisions.Collision
import game.gameObjects.GameObject
import game.undoing.IUndo

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoableCollisionEffect : ICollisionEffect {
    fun applyUndoable(source: GameObject, collision: Collision): IUndo
}