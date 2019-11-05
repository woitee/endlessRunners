package cz.woitee.endlessRunners.game.undoing

import cz.woitee.endlessRunners.game.GameState

/**
 * An interface describing something that does effects, which can be undone afterwards.
 */

interface IUndoable {
    /**
     * Perform on currentState and return how to undo it.
     */
    fun applyUndoablyOn(gameState: GameState): IUndo
}
