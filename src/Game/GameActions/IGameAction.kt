package Game.GameActions

import Game.GameState
import Game.Undoing.IApplicable

/**
 * Created by woitee on 13/01/2017.
 */

interface IGameAction: IApplicable {
    fun isApplicableOn(gameState: GameState): Boolean
    override fun applyOn(gameState: GameState)
}