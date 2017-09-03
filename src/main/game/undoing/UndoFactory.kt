package game.undoing

import game.GameState
import utils.pools.DefaultUndoListPool
import java.util.*

/**
 * UndoFactory contain
 * Created by woitee on 04/06/2017.
 */
object UndoFactory {
    class MultiUndo(val undoList: ArrayList<IUndo>): IUndo {
        override fun undo(gameState: GameState) {
            for (undo in undoList.asReversed())
                undo.undo(gameState)
            DefaultUndoListPool.returnObject(undoList)
        }
    }

    class DoubleUndo(val firstUndo: IUndo, val secondUndo: IUndo): IUndo {
        override fun undo(gameState: GameState) {
            secondUndo.undo(gameState)
            firstUndo.undo(gameState)
        }
    }

    fun doubleUndo(firstUndo: IUndo, secondUndo: IUndo): IUndo {
        return DoubleUndo(firstUndo, secondUndo)
    }

    fun multiUndo(undoList: ArrayList<IUndo>?): IUndo {
        if (undoList != null) {
            if (undoList.count() == 0) {
                DefaultUndoListPool.returnObject(undoList)
                return NoActionUndo
            }
            if (undoList.count() == 1) {
                val res = undoList[0]
                DefaultUndoListPool.returnObject(undoList)
                return res
            }
            return MultiUndo(undoList)
        }
        return NoActionUndo
    }
}