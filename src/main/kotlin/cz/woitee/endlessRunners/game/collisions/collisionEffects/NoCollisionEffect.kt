package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.NoUndo

/**
 * To not use nulls, an effect that represents no collision happening.
 */
object NoCollisionEffect : IUndoableCollisionEffect {
    override fun apply(source: GameObject, collision: Collision) {
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        return NoUndo
    }

    override fun toString(): String {
        return "NoCollisionEffect"
    }
}
