package cz.woitee.game.playerControllers

import cz.woitee.game.DummyObjects
import cz.woitee.game.GameButton
import cz.woitee.game.algorithms.dfs.BasicDFS
import cz.woitee.game.algorithms.dfs.SearchStatsSummer
import cz.woitee.game.GameState
import cz.woitee.game.algorithms.dfs.DFS
import cz.woitee.game.algorithms.dfs.delayedTwin.ButtonModel
import cz.woitee.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.game.levelGenerators.encapsulators.DFSEnsuring
import cz.woitee.game.levelGenerators.encapsulators.StateRemembering
import cz.woitee.utils.CopyUtils
import java.io.File
import java.io.ObjectOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by woitee on 09/04/2017.
 */
class DFSPlayerController(val dfs: DFS = BasicDFS(), val backupDFS: DFS? = null): PlayerController() {
    var readyToDie = false
    var logFile: PrintWriter? = null
    val statsSummer = SearchStatsSummer(sumEvery = 10000)
    var lastButtonModel: ButtonModel? = null

    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        val wasReadyToDie = readyToDie
        val action = if (readyToDie) null else performDFS(gameState)

        dfs.onUpdate(gameState.game.updateTime, action, gameState)

        if (backupDFS != null && !wasReadyToDie) {
            if (!dfs.lastStats.success && backupDFS is DelayedTwinDFS) {
                val buttonModel = backupDFS.buttonModel
                val logFileName = "out/buttonModels/ButtonModel_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp"
                println("Dumping buttonModel to $logFileName")
                val file = File(logFileName)
                val oos = ObjectOutputStream(file.outputStream())
                buttonModel.writeObject(oos)
                lastButtonModel?.writeObject(oos)
                oos.close()
            }

            if (backupDFS is DelayedTwinDFS) {
                if (lastButtonModel == null) {
                    lastButtonModel = ButtonModel(gameState.makeCopy(), gameState.makeCopy(), gameState.game.updateTime)
                }
                CopyUtils.copyBySerialization(backupDFS.buttonModel, lastButtonModel!!)
            }
            performDFS(gameState, backupDFS)
            backupDFS.onUpdate(gameState.game.updateTime, action, gameState)
        }

        return action
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

    override fun init(gameState: GameState) {
        super.init(gameState)
        dfs.init(gameState)
        backupDFS?.init(gameState)
        readyToDie = false
    }

    fun performDFS(gameState: GameState, dfs: DFS = this.dfs): GameButton.StateChange? {
        val action = dfs.searchForAction(gameState)
        if (!dfs.lastStats.success) {
            print("Reached GameOver in depth of search: ${dfs.lastStats.reachedDepth}")
            gameState.print()
            dumpState(gameState)
            readyToDie = true
        }
        logStats()
        return action
    }

    fun dumpState(gameState: GameState) {
        if (gameState.tag != "Main")
            return
        val dfsLevelGenerator = gameState.game.levelGenerator
        if (gameState.game.levelGenerator !is StateRemembering) {
            println("Couldn't dump previous states event though I wanted to.")
            return
        }

        val dfsEnsuringGenerator = (gameState.game.levelGenerator as StateRemembering?)?.innerGenerator as DFSEnsuring?

        val logFileName = "out/states/GameState_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp"
        val file = File(logFileName)
        val oos = ObjectOutputStream(file.outputStream())
//        gameState.writeObject(oos)
        if (dfsEnsuringGenerator != null) {
            dfsEnsuringGenerator.lastGameState?.writeObject(oos)
        }
        (dfsLevelGenerator as StateRemembering?)?.dumpAll()
        oos.flush()
        oos.close()
        println("Dump of state recorded in $logFileName")
    }
}