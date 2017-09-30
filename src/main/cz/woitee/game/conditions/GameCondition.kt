package cz.woitee.game.conditions

import cz.woitee.game.GameState

abstract class GameCondition {
    abstract fun isTrue(gameState: GameState): Boolean
}