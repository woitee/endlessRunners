package game.conditions

import game.GameState

abstract class GameCondition {
    abstract fun isTrue(gameState: GameState): Boolean
}