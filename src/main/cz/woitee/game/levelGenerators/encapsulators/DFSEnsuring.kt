package cz.woitee.game.levelGenerators.encapsulators

import cz.woitee.game.*
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.algorithms.DFSBase
import cz.woitee.game.levelGenerators.LevelGenerator
import cz.woitee.utils.arrayList
import java.util.*

/**
 * Created by woitee on 22/07/2017.
 */

class DFSEnsuring(val innerGenerator: LevelGenerator, val dfsProvider: DFSBase): LevelGenerator() {
    var lastGameState: GameState? = null

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        dfsProvider.reset()

        // just making copy for debug purposes, no other reason
        lastGameState = gameState.makeCopy()

//        val column = innerGenerator.generateNextColumn(gameState)
        val generatedColumn = innerGenerator.generateNextColumn(gameState)
        val droppedColumn = gameState.addColumn(generatedColumn)

        // Search it by DFS
        dfsProvider.searchForAction(gameState)
        val searchSucess = dfsProvider.lastStats.success

        // Replace gameState back into original position
        gameState.undoAddColumn(droppedColumn)

        // return column if it is feasible, otherwise, copy solid block structure from last column
        if (searchSucess) {
            return generatedColumn
        } else {
            return copySolidBlocksFromLastColumn(gameState)
        }
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