package Game.GameActions

import Game.GameState

/**
 * Created by woitee on 23/07/2017.
 */
interface IHoldAction : IGameAction {
    fun stopApplyingOn(gameState: GameState)
    fun canBeStoppedApplyingOn(gameState: GameState): Boolean
}