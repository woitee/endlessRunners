package cz.woitee.game.algorithms.dfs.delayedTwin

import cz.woitee.game.BlockWidth
import cz.woitee.game.GameState
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.algorithms.dfs.DFSBase
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.UndoFactory

/**
 * A class that provides some utility methods especially useful when searching, but might be useful even somewhere else.
 */
object DFSUtils {
    fun advanceGameStateSafely(gameState: GameState, gameAction: GameAction?, updateTime: Double): IUndo {
        if (gameAction == null)
            return gameState.advanceUndoable(updateTime)
        else {
            val firstUndo = gameAction.applyUndoablyOn(gameState)
            if (gameState.player.nextX(updateTime) + gameState.player.widthPx >= gameState.maxX) {
                // This only happens as the last update at the end of the screen, and generally is successful,
                // so really happens at MOST once per search. The extra class encapsulation overhead is negligible
                return DFSBase.NoStateAdvanceUndo(firstUndo)
            }
            val secondUndo = gameState.advanceUndoable(updateTime)
            return UndoFactory.doubleUndo(firstUndo, secondUndo)
        }
    }
}