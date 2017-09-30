package cz.woitee.game.conditions

import cz.woitee.game.GameState

class FalseCondition : GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return false
    }
}