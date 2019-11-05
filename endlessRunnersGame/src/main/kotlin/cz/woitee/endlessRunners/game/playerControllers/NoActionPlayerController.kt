package cz.woitee.endlessRunners.game.playerControllers

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState

/**
 * A player controller that does nothing.
 */
class NoActionPlayerController : PlayerController() {
    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        return null
    }
}
