package cz.woitee.endlessRunners.game.undoing

import cz.woitee.endlessRunners.game.GameState

/**
 * A singleton default value of not undoing anything, should be returned from undoable methods when nothing was changed.
 */
object NoUndo : IUndo {
    override fun undo(gameState: GameState) {
        // Do nothing
    }
}
