package Game.GameActions

import Game.GameState
import Game.Undoing.IUndo
import Game.Undoing.IUndoable

/**
 * This interface represents an action that can be undone (in simulation mode)
 *
 * Created by woitee on 09/04/2017.
 */

interface IUndoableAction: IGameAction, IUndoable