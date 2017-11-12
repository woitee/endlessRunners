package cz.woitee.game.playerControllers

import cz.woitee.game.algorithms.dfs.DFS
import cz.woitee.game.algorithms.dfs.SearchStatsSummer
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.GameState
import cz.woitee.game.algorithms.dfs.DFSBase
import cz.woitee.game.levelGenerators.encapsulators.DFSEnsuring
import cz.woitee.game.levelGenerators.encapsulators.StateRemembering
import java.io.File
import java.io.ObjectOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by woitee on 09/04/2017.
 */
class DFSPlayerController(val dfs: DFSBase = DFS()): PlayerController() {
    var readyToDie = false
    var logFile: PrintWriter? = null
    val statsSummer = SearchStatsSummer(sumEvery = 10000)

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
        val action = dfs.searchForAction(gameState)
        if (!dfs.lastStats.success) {
            gameState.print()
            dumpState(gameState)
            readyToDie = true
        }
        return action
    }

    fun dumpState(gameState: GameState) {
        if (gameState.tag != "Main")
            return
        val dfsLevelGenerator = gameState.game.levelGenerator
        val dfsEnsuringGenerator = (gameState.game.levelGenerator as StateRemembering?)?.innerGenerator as DFSEnsuring?

        val logFileName = "out/states/GameState_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp"
        val file = File(logFileName)
        val oos = ObjectOutputStream(file.outputStream())
        gameState.writeObject(oos)
        if (dfsEnsuringGenerator != null) {
            dfsEnsuringGenerator.lastGameState?.writeObject(oos)
        }
        (dfsLevelGenerator as StateRemembering?)?.dumpAll()
        oos.flush()
        oos.close()
    }
}