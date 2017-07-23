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
import Utils.pop
import java.io.File
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by woitee on 09/04/2017.
 */
class DFSPlayerController: PlayerController() {
    var readyToDie = false
    var logFile: PrintWriter? = null
    val statsSummer = SearchStatsSummer(sumEvery = 75)

    override fun onUpdate(gameState: GameState): PlayerControllerOutput? {
        if (readyToDie)
            return null

        val action = performDFS(gameState)

        if (logFile == null) {
            val logFileName = "log/DFSlog_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".csv"
            logFile = File(logFileName).printWriter()
            logFile!!.println("SearchedStates,Time")
        }
        logFile!!.println("${DFS.lastStats.searchedStates},${DFS.lastStats.timeTaken}")
        logFile!!.flush()
        statsSummer.noteStats(DFS.lastStats)
        return action?.press()
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