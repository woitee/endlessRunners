package game.collisions.collisionEffects

import game.collisions.Collision
import game.effects.UndoableGameEffect
import game.objects.GameObject
import game.undoing.IUndo

/**
 * Created by woitee on 23/01/2017.
 */

class ApplyGameEffect(val gameEffect: UndoableGameEffect): IUndoableCollisionEffect {
    override fun apply(source: GameObject, collision: Collision) {
        gameEffect.applyOn(source.gameState)
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        return gameEffect.applyUndoablyOn(source.gameState)
    }
}