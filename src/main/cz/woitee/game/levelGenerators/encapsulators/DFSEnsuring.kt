package cz.woitee.game.levelGenerators.encapsulators

import cz.woitee.game.*
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.algorithms.dfs.DFSBase
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.utils.arrayList
import java.util.*

/**
 * Created by woitee on 22/07/2017.
 */

class DFSEnsuring(val innerGenerator: LevelGenerator, val dfsProvider: DFSBase, val doDFSAfterFail: Boolean = false): LevelGenerator() {
    enum class DFSResult { NONE, SUCCESS, FAIL, FAIL_COPYCOLUMN }
    var lastGameState: GameState? = null
    var lastResult: DFSResult = DFSResult.NONE

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        // just making copy for debug purposes, no other reason
        lastGameState = gameState.makeCopy()
//        lastGameState = CopyUtils.copyBySerialization(currentState, (GameState(currentState.game, levelGenerator = currentState.levelGenerator)))

        val generatedColumn = tryDFS(gameState)
        val success = dfsProvider.lastStats.success

        // Replace currentState back into original position

        if (!dfsProvider.lastStats.success && doDFSAfterFail) {
            val columnCopy = copySolidBlocksFromLastColumn(lastGameState!!)
            tryDFS(lastGameState!!, columnCopy)
            val copyResult = if (dfsProvider.lastStats.success) "SUCCESS" else "FAIL"
            println("${gameState.gridX} Not suceeding in search even after copying identical column! Copy is $copyResult")

            lastResult = DFSResult.FAIL_COPYCOLUMN
            return columnCopy
//          currentState.print()
//          currentState.dumpToFile("NotEnsuredDump")
        }

        // return column if it is feasible, otherwise, copy solid block structure from last column
        if (success) {
            lastResult = DFSResult.SUCCESS
            return generatedColumn
        } else {
            lastResult = DFSResult.FAIL
            return copySolidBlocksFromLastColumn(gameState)
        }
    }

    /**
     * Tries to add new column, perform DFS, and remove the column to reach the original state.

     * @param gameState The currentState to perform operations with.
     * @param newColumn A column to add. If null, will use the inner generator to obtain one.
     *
     * @return The used column (gained from parameter or inner generation).
     */
    protected fun tryDFS(gameState: GameState, newColumn: ArrayList<GameObject?>? = null): ArrayList<GameObject?> {
        dfsProvider.reset()

        val generatedColumn = if (newColumn != null) newColumn else innerGenerator.generateNextColumn(gameState)
        val droppedColumn = gameState.addColumn(generatedColumn)

        // Search it by DFS
        dfsProvider.searchForAction(gameState)

        // Undo the things we have done
        gameState.undoAddColumn(droppedColumn)
        return generatedColumn
    }

    protected fun copySolidBlocksFromLastColumn(gameState: GameState): ArrayList<GameObject?> {
        val column = arrayList<GameObject?>(gameState.grid.height, { null })
        for (i in 0 until gameState.grid.height) {
            if (gameState.grid[gameState.grid.width - 1, i]?.gameObjectClass == GameObjectClass.SOLIDBLOCK)
                column[i] = SolidBlock()
        }
        return column
    }

    override fun reset() {
        innerGenerator.reset()
        dfsProvider.reset()
    }
}
