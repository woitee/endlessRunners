package cz.woitee.endlessRunners.game.playerControllers

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState

/**
 * A player controller doing random actions.
 */

class RandomPlayerController : PlayerController() {
    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        if (gameState.game.random.nextDouble() >= 0.99) {
            val actions = gameState.getPerformableButtonInteractions()
            return if (actions.isEmpty()) null else actions[0]
        }
        return null
    }
}
