package Game.PlayerControllers

import Game.GameActions.IGameAction

/**
 * Created by woitee on 23/07/2017.
 */

fun <T: IGameAction> T.press(): PlayerControllerOutput {
    return PlayerControllerOutput(this, true)
}

fun <T: IGameAction> T.release(): PlayerControllerOutput {
    return PlayerControllerOutput(this, false)
}