package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.undoing.IUndoable

/**
 * Created by woitee on 09/04/2017.
 */

abstract class UndoableGameEffect : GameEffect(), IUndoable
