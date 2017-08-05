package game.undoing

import game.GameState

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoable: IApplicable {
    /**
     * Perform on gameState and return how to undo it.
     */
    fun applyUndoablyOn(gameState: GameState): IUndo
}