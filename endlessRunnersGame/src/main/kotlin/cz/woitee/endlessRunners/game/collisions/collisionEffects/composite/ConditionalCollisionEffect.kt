package cz.woitee.endlessRunners.game.collisions.collisionEffects.composite

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ICollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.IUndoableCollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.NoCollisionEffect
import cz.woitee.endlessRunners.game.conditions.GameCondition
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * A collision effect that can have different outcomes based on a condition.
 *
 * @param condition The condition deciding on which outcome will occur
 * @param trueEffect The effect that will happen if the condition is true
 * @param falseEffect The effect that will happen if the condition is false
 */
class ConditionalCollisionEffect(
    var condition: GameCondition,
    val trueEffect: ICollisionEffect,
    val falseEffect: ICollisionEffect = NoCollisionEffect
) : IUndoableCollisionEffect {

    override fun apply(source: GameObject, collision: Collision) {
        if (condition.isTrue(source.gameState)) {
            trueEffect.apply(source, collision)
        } else {
            falseEffect.apply(source, collision)
        }
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        if (trueEffect !is IUndoableCollisionEffect || falseEffect !is IUndoableCollisionEffect)
            throw Exception("Called undo on composite Collision Effect which doens't have both effects undoable ($this)")

        return if (condition.isTrue(source.gameState)) {
            trueEffect.applyUndoable(source, collision)
        } else {
            falseEffect.applyUndoable(source, collision)
        }
    }

    override fun toString(): String {
        return "ConditionalCollisionEffect($condition, $trueEffect, $falseEffect)"
    }
}
