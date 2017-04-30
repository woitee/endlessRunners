package Game.PlayerControllers

import GUI.GamePanelVisualizer
import Game.Algorithms.DFS
import Game.Algorithms.SearchStats
import Game.Algorithms.SearchStatsSummer
import Game.GameActions.IGameAction
import Game.GameActions.IUndoableAction
import Game.GameObjects.Player
import Game.GameState
import Game.Undoing.IUndo
import Game.Undoing.MultiUndo
import Utils.pop
import java.util.*

/**
 * Created by woitee on 09/04/2017.
 */
class DFSPlayerController: PlayerController() {
    var readyToDie = false
    val statsSummer = SearchStatsSummer(sumEvery = 75)

    override fun onUpdate(gameState: GameState): IGameAction? {
        if (readyToDie)
            return null

        val action = performDFS(gameState)
        statsSummer.noteStats(DFS.lastStats)
        return action
    }

    override fun reset() {
        super.reset()
        readyToDie = false
    }

    fun performDFS(gameState: GameState): IGameAction? {
        val action = DFS.searchForAction(gameState, debug=true)
        if (!DFS.lastStats.success)
            readyToDie = true
        return action
    }
}