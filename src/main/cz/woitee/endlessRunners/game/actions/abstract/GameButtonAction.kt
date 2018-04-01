package cz.woitee.game.actions.abstract

import cz.woitee.game.GameState
import cz.woitee.game.undoing.IUndoable

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameButtonAction : IUndoable {
    abstract fun isApplicableOn(gameState: GameState): Boolean
    abstract fun applyOn(gameState: GameState)
}