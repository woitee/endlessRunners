package cz.woitee.game.algorithms.dfs.delayedTwin

import cz.woitee.game.GameButton
import cz.woitee.game.GameState
import cz.woitee.game.algorithms.dfs.DFS
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.undoing.UndoFactory

/**
 * A class that provides some utility methods especially useful when searching, but might be useful even somewhere else.
 */
object DFSUtils {
    fun advanceGameStateSafely(gameState: GameState, buttonStateChange: GameButton.StateChange?, updateTime: Double): IUndo {
        if (buttonStateChange == null)
            return gameState.advanceUndoable(updateTime)
        else {
            // We need to split the action update and gamestate update, so that action "stretching" at the end of
            // the screen doesn't cause out of range errors

            val firstUndo = buttonStateChange.applyUndoablyOn(gameState)
            if (gameState.player.nextX(updateTime) + gameState.player.widthPx >= gameState.maxX) {
                // This only happens as the last update at the end of the screen, and generally is successful,
                // so really happens at MOST once per search. The extra class encapsulation overhead is negligible
                return DFS.NoStateAdvanceUndo(firstUndo)
            }
            val secondUndo = gameState.advanceUndoable(updateTime, buttonStateChange.gameButton.action)
            return UndoFactory.doubleUndo(firstUndo, secondUndo)
        }
    }
}