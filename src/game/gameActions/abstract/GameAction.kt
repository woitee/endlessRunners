package game.gameActions.abstract

import game.GameState
import game.undoing.IApplicable

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameAction : IApplicable {
    abstract fun isApplicableOn(gameState: GameState): Boolean
}