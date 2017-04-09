package Game.PlayerControllers

import Game.GameActions.IGameAction
import Game.GameActions.JumpAction
import Game.GameState
import Game.PlayerControllers.PlayerController

import java.util.Random

/**
 * Created by woitee on 14/01/2017.
 */

class RandomPlayerController: PlayerController() {

    override fun onUpdate(gameState: GameState): IGameAction? {
        if (gameState.game.random.nextDouble() >= 0.99) {
            val actions = gameState.getPerformableActions()
            return if (actions.isEmpty()) null else actions[0]
        }
        return null
    }
}