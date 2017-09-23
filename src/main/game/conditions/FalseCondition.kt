package game.conditions

import game.GameState

class FalseCondition : GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return false
    }
}