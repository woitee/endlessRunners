package cz.woitee.game.undoing

import cz.woitee.game.GameState

/**
 * Created by woitee on 09/04/2017.
 */

interface IUndoable: IApplicable {
    /**
     * Perform on gameState and return how to undo it.
     */
    fun applyUndoablyOn(gameState: GameState): IUndo
}