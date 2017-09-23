package game.playerControllers

import game.algorithms.DFS
import game.algorithms.SearchStatsSummer
import game.actions.abstract.GameAction
import game.GameState
import game.levelGenerators.DFSEnsuringGenerator
import java.io.File
import java.io.ObjectOutputStream
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
        logStats()

        return action?.press()
    }

    fun logStats() {
        if (logFile == null) {
            val datestring = SimpleDateFormat("yyyy_MM_dd").format(Date())
            val datetimestring = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date())
            val logFolderName = "out/log/$datestring"
            val logFileName = "$logFolderName/DFSlog_$datetimestring.csv"
            File(logFolderName).mkdirs()
            logFile = File(logFileName).printWriter()
            logFile!!.println("SearchedStates,Time")
        }
        logFile!!.println("${dfs.lastStats.searchedStates},${dfs.lastStats.timeTaken}")
        logFile!!.flush()
        statsSummer.noteStats(dfs.lastStats)
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
            dumpState(gameState)
            readyToDie = true
        }
        return action
    }

    fun dumpState(gameState: GameState) {
        val dfsLevelGenerator = gameState.game.levelGenerator as? DFSEnsuringGenerator

        val logFileName = "out/states/GameState_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp"
        val file = File(logFileName)
        val oos = ObjectOutputStream(file.outputStream())
        gameState.writeObject(oos)
        if (dfsLevelGenerator != null) {
            dfsLevelGenerator.lastGameState?.writeObject(oos)
        }
        oos.flush()
        oos.close()
    }
}