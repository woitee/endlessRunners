package Game.Undoing

import Game.GameState

/**
 * Undo action that groups several undoings in a higher-level single undo action.
 *
 * Created by woitee on 09/04/2017.
 */
class MultiUndo(val undoList: List<IUndo>): IUndo {
    override fun undo(gameState: GameState) {
        for (undo in undoList.asReversed())
            undo.undo(gameState)
    }
}