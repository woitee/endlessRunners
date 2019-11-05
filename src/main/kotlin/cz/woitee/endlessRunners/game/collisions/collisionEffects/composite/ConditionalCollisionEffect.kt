package cz.woitee.endlessRunners.game.collisions.collisionEffects.composite

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.collisions.collisionEffects.IUndoableCollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.IgnoreCollision
import cz.woitee.endlessRunners.game.conditions.GameCondition
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo

class ConditionalCollisionEffect(
    val condition: GameCondition,
    val trueEffect: IUndoableCollisionEffect,
    val falseEffect: IUndoableCollisionEffect = IgnoreCollision()
) : IUndoableCollisionEffect {

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
