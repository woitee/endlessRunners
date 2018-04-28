package cz.woitee.endlessRunners.game.undoing

import cz.woitee.endlessRunners.game.GameState

/**
 * Extensible interface which represents a class holding info about
 * how to undo an event.
 *
 * Created by woitee on 09/04/2017.
 */

interface IUndo {
    fun undo(gameState: GameState)
}