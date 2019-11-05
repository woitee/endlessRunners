package cz.woitee.endlessRunners.game.tracking

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.collisions.collisionEffects.IUndoableCollisionEffect
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A wrapper for a collision effect that tracks times applied and undone.
 */
class TrackingCollisionEffect(val innerEffect: IUndoableCollisionEffect) : IUndoableCollisionEffect {
    var timesApplied = 0
    var timesUndone = 0

    override fun apply(source: GameObject, collision: Collision) {
        innerEffect.apply(source, collision)
        ++timesApplied
    }
    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        val innerUndo = innerEffect.applyUndoable(source, collision)
        ++timesApplied

        return object : IUndo {
            override fun undo(gameState: GameState) {
                --timesApplied
                innerUndo.undo(gameState)
                ++timesUndone
            }
        }
    }

    override fun toString(): String {
        return innerEffect.toString()
    }
}
