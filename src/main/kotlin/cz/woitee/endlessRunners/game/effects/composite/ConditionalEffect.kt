package cz.woitee.endlessRunners.game.effects.composite

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.conditions.GameCondition
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.NoEffect
import cz.woitee.endlessRunners.game.effects.UndoableGameEffect
import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * An effect that can have different outcomes based on a condition.
 *
 * @param condition The condition based on which different results occur
 * @param trueEffect The effect that occurs when the condition is true
 * @param falseEffect The effect that occurs when the condition is false
 */
class ConditionalEffect(var condition: GameCondition, val trueEffect: GameEffect, val falseEffect: GameEffect = NoEffect) : UndoableGameEffect() {
    override fun applyOn(gameState: GameState) {
        if (condition.isTrue(gameState)) {
            trueEffect.applyOn(gameState)
        } else {
            falseEffect.applyOn(gameState)
        }
    }

    override fun applyUndoablyOn(gameState: GameState): IUndo {
        if (trueEffect !is UndoableGameEffect || falseEffect !is UndoableGameEffect)
            throw java.lang.Exception("Called undo on composite Collision Effect which doens't have both effects undoable ($this)")

        return if (condition.isTrue(gameState)) {
            trueEffect.applyUndoablyOn(gameState)
        } else {
            falseEffect.applyUndoablyOn(gameState)
        }
    }

    override fun toString(): String {
        return "ConditionalEffect($condition, $trueEffect, $falseEffect)"
    }
}
