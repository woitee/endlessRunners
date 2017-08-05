package Game.GameActions

import Game.GameState
import Game.Undoing.IApplicable

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameAction : IApplicable {
    abstract fun isApplicableOn(gameState: GameState): Boolean
}