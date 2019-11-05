package cz.woitee.endlessRunners.game.playerControllers

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState

/**
 * A player controller - component to control the player in the game.
 */
abstract class PlayerController {
    /**
     * Performs a decision and returns the desired action in a given GameState.
     */
    abstract fun onUpdate(gameState: GameState): GameButton.StateChange?

    /**
     * Called when a new GameState is created, to restart internal state if there is any.
     */
    open fun init(gameState: GameState) {}
}
