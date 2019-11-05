package cz.woitee.endlessRunners.game.effects.composite

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.conditions.GameCondition
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.NoEffect

class ConditionalEffect(val condition: GameCondition, val trueEffect: GameEffect, val falseEffect: GameEffect = NoEffect): GameEffect() {
    override fun applyOn(gameState: GameState) {
        if (condition.isTrue(gameState)) {
            trueEffect.applyOn(gameState)
        } else {
            falseEffect.applyOn(gameState)
        }
    }
}