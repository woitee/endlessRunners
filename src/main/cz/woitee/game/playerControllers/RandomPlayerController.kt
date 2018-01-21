package cz.woitee.game.playerControllers

import cz.woitee.game.GameButton
import cz.woitee.game.GameState

/**
 * Created by woitee on 14/01/2017.
 */

class RandomPlayerController: PlayerController() {
    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        if (gameState.game.random.nextDouble() >= 0.99) {
            val actions = gameState.getPerformableButtonInteractions()
            return if (actions.isEmpty()) null else actions[0]
        }
        return null
    }
}