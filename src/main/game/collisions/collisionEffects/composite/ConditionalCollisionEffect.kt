package game.collisions.collisionEffects.composite

import game.collisions.Collision
import game.collisions.collisionEffects.IUndoableCollisionEffect
import game.collisions.collisionEffects.IgnoreCollision
import game.conditions.GameCondition
import game.objects.GameObject
import game.undoing.IUndo

class ConditionalCollisionEffect(
        val condition: GameCondition,
        val trueEffect: IUndoableCollisionEffect,
        val falseEffect: IUndoableCollisionEffect = IgnoreCollision
    ): IUndoableCollisionEffect {

    override fun apply(source: GameObject, collision: Collision) {
        if (condition.isTrue(source.gameState)) {
            trueEffect.apply(source, collision)
        } else {
            falseEffect.apply(source, collision)
        }
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        return if (condition.isTrue(source.gameState)) {
            trueEffect.applyUndoable(source, collision)
        } else {
            falseEffect.applyUndoable(source, collision)
        }
    }
}