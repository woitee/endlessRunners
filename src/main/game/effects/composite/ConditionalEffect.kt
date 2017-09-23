package game.effects.composite

import game.GameState
import game.conditions.GameCondition
import game.effects.GameEffect
import game.effects.NoEffect

class ConditionalEffect(val condition: GameCondition, val trueEffect: GameEffect, val falseEffect: GameEffect = NoEffect): GameEffect() {
    override fun applyOn(gameState: GameState) {
        if (condition.isTrue(gameState)) {
            trueEffect.applyOn(gameState)
        } else {
            falseEffect.applyOn(gameState)
        }
    }
}