package cz.woitee.game.effects.composite

import cz.woitee.game.GameState
import cz.woitee.game.conditions.GameCondition
import cz.woitee.game.effects.GameEffect
import cz.woitee.game.effects.NoEffect

class ConditionalEffect(val condition: GameCondition, val trueEffect: GameEffect, val falseEffect: GameEffect = NoEffect): GameEffect() {
    override fun applyOn(gameState: GameState) {
        if (condition.isTrue(gameState)) {
            trueEffect.applyOn(gameState)
        } else {
            falseEffect.applyOn(gameState)
        }
    }
}