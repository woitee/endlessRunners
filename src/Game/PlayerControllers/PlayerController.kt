package Game.PlayerControllers

import Game.GameActions.IGameAction
import Game.GameState

/**
 * Created by woitee on 13/01/2017.
 */

abstract class PlayerController {
    abstract fun onUpdate(gameState: GameState): IGameAction?
    open fun reset() {}
}