package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A collision effects that results in one object destroying the one it collided with.
 */
class DestroyOther : IUndoableCollisionEffect {
    override fun apply(source: GameObject, collision: Collision) {
        source.gameState.remove(collision.other)
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        apply(source, collision)
        return object : IUndo {
            override fun undo(gameState: GameState) {
                gameState.add(collision.other)
            }
        }
    }

    override fun toString(): String {
        return "DestroyOther"
    }
}
