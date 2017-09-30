package cz.woitee.game.algorithms

import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.actions.abstract.UndoableAction
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.UndoFactory
import java.util.*

class DelayedTwinDFS(val delayTime: Double = -1.0, maxDepth: Int = 1000, debug: Boolean = true): DFSBase(true, maxDepth, debug) {
    override fun searchInternal(gameState: GameState, updateTime: Double): SearchResult {
        val delayedState = gameState.makeCopy()

        val framesDelayed = Math.ceil(delayTime / updateTime).toInt()
        val delayUndosStack = ArrayList<IUndo>()
        for (i in 1 .. framesDelayed) {
            delayUndosStack.add(advanceState(gameState, null))
        }

        // temporary
        return SearchResult(true, null)
    }
}