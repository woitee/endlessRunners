package cz.woitee.game.levelGenerators.encapsulators

import cz.woitee.game.*
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.algorithms.dfs.DFS
import cz.woitee.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.utils.arrayList
import cz.woitee.utils.dumpToFile
import java.io.File
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by woitee on 22/07/2017.
 */

open class DFSEnsuring(val innerGenerator: LevelGenerator, val dfsProvider: DFS, val doDFSAfterFail: Boolean = false, val dumpErrors: Boolean = true): LevelGenerator() {
    enum class DFSResult { NONE, SUCCESS, FAIL, FAIL_COPYCOLUMN }
    var lastGameState: GameState? = null
    var lastResult: DFSResult = DFSResult.NONE

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        // just making copy for debug purposes, no other reason
        lastGameState = gameState.makeCopy()

        val generatedColumn = tryDFS(gameState)
        val success = dfsProvider.lastStats.success

        if (!success && doDFSAfterFail) {
            val columnCopy = copySolidBlocksFromLastColumn(lastGameState!!)
            tryDFS(lastGameState!!, columnCopy)
            if (!dfsProvider.lastStats.success) {
                println("${gameState.gridX} Not suceeding in search even after copying identical column!")
                lastResult = DFSResult.FAIL_COPYCOLUMN
                if (dfsProvider is DelayedTwinDFS) {
                    val buttonModel = dfsProvider.buttonModel!!
                    val logFileName = "out/buttonModels/LevelGenButtonModel_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp"
                    println("Dumping buttonModel to $logFileName")
                    val file = File(logFileName)
                    val oos = ObjectOutputStream(file.outputStream())
                    buttonModel.writeObject(oos)
                    oos.close()
                }
//                if (dumpErrors)
//                    lastGameState?.dumpToFile("NotEnsuredDump")
//                return columnCopy
            }
        }

        // return column if it is feasible, otherwise, copy solid block structure from last column
        return if (success) {
            lastResult = DFSResult.SUCCESS
            generatedColumn
        } else {
            lastResult = DFSResult.FAIL
            this.generateBackupColumn(gameState)
        }
    }

    /**
     * Tries to add new column, perform a DFS, and remove the column to reach the original state.

     * @param gameState The currentState to perform operations with.
     * @param newColumn A column to add. If null, will use the inner generator to obtain one.
     *
     * @return The used column (gained from parameter or inner generation).
     */
    protected open fun tryDFS(gameState: GameState, newColumn: ArrayList<GameObject?>? = null): ArrayList<GameObject?> {
        beforeDFS(gameState)

        val generatedColumn = newColumn ?: innerGenerator.generateNextColumn(gameState)
        val droppedColumn = gameState.addColumn(generatedColumn)

        // Search it by BasicDFS
        dfsProvider.searchForAction(gameState)

        // Undo the things we have done
        gameState.undoAddColumn(droppedColumn)

        return generatedColumn
    }

    fun copySolidBlocksFromLastColumn(gameState: GameState): ArrayList<GameObject?> {
        val column = arrayList<GameObject?>(gameState.grid.height, { null })
        for (i in 0 until gameState.grid.height) {
            if (gameState.grid[gameState.grid.width - 1, i]?.gameObjectClass == GameObjectClass.SOLIDBLOCK)
                column[i] = SolidBlock()
        }
        return column
    }

    protected open fun beforeDFS(gameState: GameState) {
        dfsProvider.init(gameState)
    }

    /**
     * Called whenever BasicDFS fails to retrieve a different column. Default behaviour is to return
     * solid blocks from last column in gamestate, and blank for all other blocks.
     */
    protected open fun generateBackupColumn(gameState: GameState): ArrayList<GameObject?> {
        return copySolidBlocksFromLastColumn(gameState)
    }

    override fun init(gameState: GameState) {
        innerGenerator.init(gameState)
        dfsProvider.init(gameState)
    }
}
