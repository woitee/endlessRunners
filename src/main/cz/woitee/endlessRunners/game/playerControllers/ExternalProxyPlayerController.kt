package cz.woitee.game.playerControllers

import cz.woitee.game.GameButton
import cz.woitee.game.GameState

class ExternalProxyPlayerController: PlayerController() {
    var desiredStateChange: GameButton.StateChange? = null

    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        return desiredStateChange
    }
}