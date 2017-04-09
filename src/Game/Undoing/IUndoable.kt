package Game.Undoing

import Game.GameState

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoable: IApplicable {
    /**
     * Perform on gameState and return how to undo it.
     */
    fun applyUndoableOn(gameState: GameState): IUndo
}