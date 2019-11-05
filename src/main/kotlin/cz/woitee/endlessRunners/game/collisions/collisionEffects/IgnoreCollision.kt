package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

class IgnoreCollision : IUndoableCollisionEffect {
    override fun apply(source: GameObject, collision: Collision) {
        // do nothing
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        return NoUndo
    }
}
