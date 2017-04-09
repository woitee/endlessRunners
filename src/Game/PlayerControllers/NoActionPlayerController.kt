package Game.PlayerControllers

import Game.GameActions.IGameAction
import Game.GameState

/**
 * Created by woitee on 14/01/2017.
 */

class NoActionPlayerController: PlayerController() {
    override fun onUpdate(gameState: GameState): IGameAction? {
        return null
    }
}