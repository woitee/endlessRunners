package cz.woitee.game.actions.abstract

import cz.woitee.game.GameState
import cz.woitee.game.undoing.IApplicable
import cz.woitee.game.undoing.IUndoable

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameAction : IApplicable, IUndoable {
    abstract fun isApplicableOn(gameState: GameState): Boolean
}