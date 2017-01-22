package Game.GameActions

import Game.GameState

/**
 * Created by woitee on 13/01/2017.
 */

interface IGameAction {
    fun isPerformableOn(gameState: GameState): Boolean
    fun performOn(gameState: GameState)
}