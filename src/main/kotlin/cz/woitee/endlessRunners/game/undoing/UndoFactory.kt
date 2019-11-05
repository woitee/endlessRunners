package cz.woitee.endlessRunners.game.undoing

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.utils.pools.DefaultUndoListPool
import java.util.*

/**
 * UndoFactory contains methods of adding undos together.
 */
object UndoFactory {
    /**
     * An undo containing multiple undos in it.
     */
    class MultiUndo(val undoList: ArrayList<IUndo>) : IUndo {
        override fun undo(gameState: GameState) {
            for (undo in undoList.asReversed())
                undo.undo(gameState)
            DefaultUndoListPool.returnObject(undoList)
        }
    }

    /**
     * An undo containing two undos in it, to save the allocation of an ArrayList.
     */
    class DoubleUndo(val firstUndo: IUndo, val secondUndo: IUndo) : IUndo {
        override fun undo(gameState: GameState) {
            secondUndo.undo(gameState)
            firstUndo.undo(gameState)
        }
    }

    /**
     * Combine two undos in an undo.
     */
    fun doubleUndo(firstUndo: IUndo, secondUndo: IUndo): IUndo {
        return DoubleUndo(firstUndo, secondUndo)
    }

    /**
     * Combine multiple undos in an undo. For two objects, use the doubleUndo method.
     */
    fun multiUndo(undoList: ArrayList<IUndo>?): IUndo {
        if (undoList != null) {
            if (undoList.count() == 0) {
                DefaultUndoListPool.returnObject(undoList)
                return NoUndo
            }
            if (undoList.count() == 1) {
                val res = undoList[0]
                DefaultUndoListPool.returnObject(undoList)
                return res
            }
            return MultiUndo(undoList)
        }
        return NoUndo
    }
}
