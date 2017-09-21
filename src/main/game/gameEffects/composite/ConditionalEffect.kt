package game.gameEffects.composite

import game.GameState
import game.gameConditions.GameCondition
import game.gameEffects.GameEffect
import game.gameEffects.NoEffect

class ConditionalEffect(val condition: GameCondition, val trueEffect: GameEffect, val falseEffect: GameEffect = NoEffect): GameEffect() {
    override fun applyOn(gameState: GameState) {
        if (condition.isTrue(gameState)) {
            trueEffect.applyOn(gameState)
        } else {
            falseEffect.applyOn(gameState)
        }
    }
}