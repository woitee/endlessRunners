package Game.GameActions

import Game.GameState
import Game.Undoing.IUndo
import Game.Undoing.IUndoable

/**
 * Created by woitee on 23/07/2017.
 */
interface IUndoableHoldAction: IHoldAction, IUndoableAction {
    fun stopApplyingUndoablyOn(gameState: GameState): IUndo
}