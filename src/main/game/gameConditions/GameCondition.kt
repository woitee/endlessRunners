package game.gameConditions

import game.GameState

abstract class GameCondition {
    abstract fun holds(gameState: GameState): Boolean
}