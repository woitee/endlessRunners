package Game.Collisions.CollisionEffects

import Game.Collisions.Collision
import Game.GameObjects.GameObject
import Game.Undoing.IUndo

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoableCollisionEffect : ICollisionEffect {
    fun applyUndoable(source: GameObject, collision: Collision): IUndo
}