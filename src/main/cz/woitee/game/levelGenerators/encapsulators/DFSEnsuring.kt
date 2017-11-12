package cz.woitee.game.levelGenerators.encapsulators

import cz.woitee.game.*
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.algorithms.DFSBase
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.utils.CopyUtils
import cz.woitee.utils.arrayList
import java.util.*
import cz.woitee.utils.dumpToFile

/**
 * Created by woitee on 22/07/2017.
 */

class DFSEnsuring(val innerGenerator: LevelGenerator, val dfsProvider: DFSBase, val doDFSAfterFail: Boolean = false): LevelGenerator() {
    var lastGameState: GameState? = null

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        // just making copy for debug purposes, no other reason
        lastGameState = gameState.makeCopy()
//        lastGameState = CopyUtils.copyBySerialization(gameState, (GameState(gameState.game, levelGenerator = gameState.levelGenerator)))

        val generatedColumn = tryDFS(gameState)
        val success = dfsProvider.lastStats.success

        // Replace gameState back into original position

        if (!dfsProvider.lastStats.success && doDFSAfterFail) {
            val retriedSuccess = tryDFS(gameState, copySolidBlocksFromLastColumn(gameState))
            if (!dfsProvider.lastStats.success) {
                tryDFS(lastGameState!!, copySolidBlocksFromLastColumn(lastGameState!!))
                val copyResult = if (dfsProvider.lastStats.success) "SUCCESS" else "FAIL"
                println("${gameState.gridX} Not suceeding in search even after copying identical column! Copy is $copyResult")
//                gameState.print()
//                gameState.dumpToFile("NotEnsuredDump")
            }
        }

        // return column if it is feasible, otherwise, copy solid block structure from last column
        if (success) {
            return generatedColumn
        } else {
            return copySolidBlocksFromLastColumn(gameState)
        }
    }

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
