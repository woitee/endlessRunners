package game.undoing

import game.GameState

/**
 * Created by woitee on 04/06/2017.
 */
object NoActionUndo: IUndo {
    override fun undo(gameState: GameState) {
        // Do nothing
    }
}