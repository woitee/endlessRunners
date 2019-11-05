package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState

/**
 * A condition that is always true.
 */
class TrueCondition : GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        return true
    }
}
