package cz.woitee.game.actions.abstract

import cz.woitee.game.GameState
import cz.woitee.game.undoing.IApplicable

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameAction : IApplicable {
    abstract fun isApplicableOn(gameState: GameState): Boolean
}