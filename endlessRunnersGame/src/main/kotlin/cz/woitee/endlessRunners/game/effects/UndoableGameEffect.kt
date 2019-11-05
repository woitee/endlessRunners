package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.undoing.IUndoable

/**
 * An effect that can be undone to reach previous state.
 */

abstract class UndoableGameEffect : GameEffect(), IUndoable
