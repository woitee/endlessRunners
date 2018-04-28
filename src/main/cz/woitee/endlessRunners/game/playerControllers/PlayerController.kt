package cz.woitee.endlessRunners.game.playerControllers

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.GameState

/**
 * Created by woitee on 13/01/2017.
 */

abstract class PlayerController {
    // button mode - if set to true, the controller returns presses of releases of buttons
    // specifically, it also returns releases of non-hold actions
    abstract fun onUpdate(gameState: GameState): GameButton.StateChange?
    open fun init(gameState: GameState) {}
}