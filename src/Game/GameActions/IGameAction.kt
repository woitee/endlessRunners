package Game.GameActions

import Game.GameState

/**
 * Created by woitee on 13/01/2017.
 */

interface IGameAction {
    fun performOn(gameState: GameState)
}