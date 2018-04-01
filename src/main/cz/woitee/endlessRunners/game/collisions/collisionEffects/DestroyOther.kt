package cz.woitee.game.collisions.collisionEffects

import cz.woitee.game.GameState
import cz.woitee.game.collisions.Collision
import cz.woitee.game.objects.GameObject
import cz.woitee.game.undoing.IUndo

class DestroyOther: IUndoableCollisionEffect {
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
}