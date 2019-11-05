package cz.woitee.endlessRunners.game.undoing

import cz.woitee.endlessRunners.game.GameState

/**
 * Created by woitee on 04/06/2017.
 */
object NoUndo : IUndo {
    override fun undo(gameState: GameState) {
        // Do nothing
    }
}