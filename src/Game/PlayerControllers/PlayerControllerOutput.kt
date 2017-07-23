package Game.PlayerControllers

import Game.GameActions.IGameAction

/**
 * Created by woitee on 23/07/2017.
 */
data class PlayerControllerOutput(val action: IGameAction, val press: Boolean)