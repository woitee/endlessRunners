package game.collisions.collisionEffects

import game.GameState
import game.collisions.Collision
import game.objects.GameObject
import game.undoing.IUndo

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