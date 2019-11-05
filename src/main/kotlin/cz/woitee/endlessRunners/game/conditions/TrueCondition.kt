package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState

class TrueCondition : GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return true
    }
}
