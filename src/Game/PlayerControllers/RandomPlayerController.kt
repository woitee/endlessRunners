package Game.PlayerControllers

import Game.GameActions.IGameAction
import Game.GameActions.JumpAction
import Game.GameState
import Game.PlayerControllers.IPlayerController

import java.util.Random

/**
 * Created by woitee on 14/01/2017.
 */

class RandomPlayerController: IPlayerController {
    val random = Random()

    override fun onUpdate(gameState: GameState): IGameAction? {
        if (random.nextDouble() >= 0.99)
            return JumpAction()
        return null
    }
}