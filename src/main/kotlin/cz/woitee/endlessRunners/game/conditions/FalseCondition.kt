package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState

class FalseCondition : GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return false
    }
}