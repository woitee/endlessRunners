package cz.woitee.endlessRunners.game.playerControllers

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState

/**
 * Created by woitee on 14/01/2017.
 */

class NoActionPlayerController : PlayerController() {
    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        return null
    }
}
