package cz.woitee.game.collisions.collisionEffects

import cz.woitee.game.collisions.Collision
import cz.woitee.game.objects.GameObject
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.NoUndo

object IgnoreCollision: IUndoableCollisionEffect {
    override fun apply(source: GameObject, collision: Collision) {
        // do nothing
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        return NoUndo
    }
}