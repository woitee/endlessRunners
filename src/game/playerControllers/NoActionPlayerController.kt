package game.playerControllers

import game.GameState

/**
 * Created by woitee on 14/01/2017.
 */

class NoActionPlayerController: PlayerController() {
    override fun onUpdate(gameState: GameState): PlayerControllerOutput? {
        return null
    }
}