package game.playerControllers

import game.actions.abstract.GameAction

/**
 * Created by woitee on 23/07/2017.
 */
data class PlayerControllerOutput(val action: GameAction, val press: Boolean)