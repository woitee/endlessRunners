package cz.woitee.endlessRunners.game.undoing

import cz.woitee.endlessRunners.game.GameState

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoable {
    /**
     * Perform on currentState and return how to undo it.
     */
    fun applyUndoablyOn(gameState: GameState): IUndo
}