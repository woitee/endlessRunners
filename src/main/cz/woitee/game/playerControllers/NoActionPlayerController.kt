package cz.woitee.game.playerControllers

import cz.woitee.game.GameButton
import cz.woitee.game.GameState

/**
 * Created by woitee on 14/01/2017.
 */

class NoActionPlayerController: PlayerController() {
    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        return null
    }
}