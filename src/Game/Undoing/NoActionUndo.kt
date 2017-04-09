package Game.Undoing

import Game.GameState

/**
 * Specific undo object that reverts nothing (as a singleton).
 * Instead of returning nulls from undoable calls when nothing happens, return this.
 *
 * Created by woitee on 09/04/2017.
 */

object NoActionUndo: IUndo {
    override fun undo(gameState: GameState) {
    }
}
