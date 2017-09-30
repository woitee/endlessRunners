package cz.woitee.game.playerControllers

import cz.woitee.game.GameState

/**
 * Created by woitee on 14/01/2017.
 */

class RandomPlayerController: PlayerController() {
    override fun onUpdate(gameState: GameState): PlayerControllerOutput? {
        if (gameState.game.random.nextDouble() >= 0.99) {
            val actions = gameState.getPerformableActions()
            return if (actions.isEmpty()) null else actions[0].press()
        }
        return null
    }
}