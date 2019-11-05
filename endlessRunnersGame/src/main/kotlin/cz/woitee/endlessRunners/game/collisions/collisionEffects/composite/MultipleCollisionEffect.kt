package cz.woitee.endlessRunners.game.collisions.collisionEffects.composite

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.collisions.collisionEffects.IUndoableCollisionEffect
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo
import cz.woitee.endlessRunners.game.undoing.UndoFactory
import cz.woitee.endlessRunners.utils.pools.DefaultUndoListPool

/**
 * A combination effect that applies several effects at the same time.
 *
 * @param innerEffects The effects that will occur whener this effect does.
 */
class MultipleCollisionEffect(vararg val innerEffects: IUndoableCollisionEffect) : IUndoableCollisionEffect {

    override fun apply(source: GameObject, collision: Collision) {
        for (effect in innerEffects) {
            effect.apply(source, collision)
        }
    }

    override fun applyUndoable(source: GameObject, collision: Collision): IUndo {
        val undoList = DefaultUndoListPool.borrowObject()
        for (effect in innerEffects) {
            undoList.add(effect.applyUndoable(source, collision))
        }
        return UndoFactory.MultiUndo(undoList)
    }

    override fun toString(): String {
        return "MultipleCollisionEffect(${innerEffects.joinToString(",")})"
    }
}
