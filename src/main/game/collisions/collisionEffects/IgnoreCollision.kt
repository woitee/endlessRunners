package game.collisions.collisionEffects

import game.collisions.Collision
import game.gameObjects.GameObject
import game.undoing.IUndo
import game.undoing.NoActionUndo

object IgnoreCollision: IUndoableCollisionEffect {
    override fun apply(source: GameObject, collision: Collision) {
        // do nothing
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        return NoActionUndo
    }
}