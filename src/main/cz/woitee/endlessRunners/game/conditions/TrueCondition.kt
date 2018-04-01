package cz.woitee.game.conditions

import cz.woitee.game.GameState

class TrueCondition: GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return true
    }
}