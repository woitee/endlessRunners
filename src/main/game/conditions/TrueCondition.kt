package game.conditions

import game.GameState

class TrueCondition: GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return true
    }
}