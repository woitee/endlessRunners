package game.playerControllers

import game.algorithms.DFS
import game.algorithms.SearchStatsSummer
import game.gameActions.abstract.GameAction
import game.GameState
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
    val statsSummer = SearchStatsSummer(sumEvery = 10000)
    val dfs = DFS()

    override fun onUpdate(gameState: GameState): PlayerControllerOutput? {
        if (readyToDie)
            return null

        val action = performDFS(gameState)

        if (logFile == null) {
            val logFileName = "log/DFSlog_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".csv"
            logFile = File(logFileName).printWriter()
            logFile!!.println("SearchedStates,Time")
        }
        logFile!!.println("${dfs.lastStats.searchedStates},${dfs.lastStats.timeTaken}")
        logFile!!.flush()
        statsSummer.noteStats(dfs.lastStats)
        return action?.press()
    }

    override fun reset() {
        super.reset()
        dfs.reset()
        readyToDie = false
    }

    fun performDFS(gameState: GameState): GameAction? {
        val action = dfs.searchForAction(gameState, debug=true)
        if (!dfs.lastStats.success) {
            gameState.print()
            readyToDie = true
        }
        return action
    }
}