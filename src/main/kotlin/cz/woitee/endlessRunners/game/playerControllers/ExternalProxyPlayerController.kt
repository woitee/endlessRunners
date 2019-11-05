package cz.woitee.endlessRunners.game.playerControllers

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState

class ExternalProxyPlayerController : PlayerController() {
    var desiredStateChange: GameButton.StateChange? = null

    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        return desiredStateChange
    }
}
