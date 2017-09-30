package cz.woitee.game.actions.abstract

import cz.woitee.game.undoing.IUndoable

/**
 * This interface represents an action that can be undone (in simulation mode)
 *
 * Created by woitee on 09/04/2017.
 */

abstract class UndoableAction : GameAction(), IUndoable