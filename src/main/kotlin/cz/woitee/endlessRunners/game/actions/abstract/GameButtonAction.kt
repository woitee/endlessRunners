package cz.woitee.endlessRunners.game.actions.abstract

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.undoing.IUndoable

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameButtonAction : IUndoable {
    abstract fun isApplicableOn(gameState: GameState): Boolean
    abstract fun applyOn(gameState: GameState)
}
