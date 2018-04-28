package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState

abstract class GameCondition {
    abstract fun isTrue(gameState: GameState): Boolean
}