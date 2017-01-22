package Game.PlayerControllers

import Game.GameActions.IGameAction
import Game.GameActions.JumpAction
import Game.GameState
import Game.PlayerControllers.IPlayerController

/**
 * Created by woitee on 14/01/2017.
 */

class RandomPlayerController: IPlayerController {
    override fun onUpdate(gameState: GameState): IGameAction? {
        if (gameState.gridX == 5)
            return JumpAction()
        return null
    }
}