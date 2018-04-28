package cz.woitee.endlessRunners.game.collisions.collisionEffects

import cz.woitee.endlessRunners.game.collisions.Collision
import cz.woitee.endlessRunners.game.effects.UndoableGameEffect
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.undoing.IUndo

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