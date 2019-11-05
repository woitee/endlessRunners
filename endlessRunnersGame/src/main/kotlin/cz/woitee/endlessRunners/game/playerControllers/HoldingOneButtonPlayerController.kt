package cz.woitee.endlessRunners.game.playerControllers

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState

/**
 * A very basic player, that just keeps holding one button.
 *
 * @param buttonIx index of the button to hold
 */
class HoldingOneButtonPlayerController(val buttonIx: Int) : PlayerController() {
    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        val button = gameState.buttons[buttonIx]
        return if (!button.isPressed) button.hold else null
    }
}
