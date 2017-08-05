package Game.PlayerControllers

import Game.GameActions.GameAction

/**
 * Created by woitee on 23/07/2017.
 */

fun <T: GameAction> T.press(): PlayerControllerOutput {
    return PlayerControllerOutput(this, true)
}

fun <T: GameAction> T.release(): PlayerControllerOutput {
    return PlayerControllerOutput(this, false)
}