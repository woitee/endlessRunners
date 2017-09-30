package cz.woitee.game.levelGenerators

import cz.woitee.game.GameState
import cz.woitee.game.Grid2D
import cz.woitee.game.HeightBlocks
import cz.woitee.game.WidthBlocks
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.*
import cz.woitee.game.algorithms.DFS
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.*
import java.util.*

/**
 * Created by woitee on 22/07/2017.
 */

class DFSEnsuringGenerator(val innerGenerator: ILevelGenerator): ILevelGenerator {
    var dfs = DFS()
    var lastGameState: GameState? = null

    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
        val originalDFS = dfs.makeCopy()
        lastGameState = gameState.makeCopy()
        val column = innerGenerator.generateNextColumn(gameState)

        // Create a new grid with added column
        val testGrid = Grid2D<GameObject?>(WidthBlocks + 1, HeightBlocks, { null })
        for (x in 0 .. gameState.grid.width - 1) {
            for (y in 0 .. gameState.grid.height - 1) {
                testGrid[x, y] = gameState.grid[x, y]
            }
        }
        // Exchange it into gameState
        val oldGrid = gameState.grid
        gameState.grid = testGrid
        for (y in 0 .. column.lastIndex) {
            gameState.addToGrid(column[y], oldGrid.width - 1, y)
        }

        // Search it by DFS
        dfs.searchForAction(gameState)
        val searchSucess = dfs.lastStats.success

        // Replace gameState back into original position
        for (y in 0 .. column.lastIndex) {
            gameState.remove(column[y])
        }
        gameState.grid = oldGrid

        // return column if it is feasible, otherwise, copy SolidBlocks from last column
        if (!searchSucess){
            for (y in 0 .. column.lastIndex) {
                if (gameState.grid[gameState.grid.width - 2, y]?.gameObjectClass == GameObjectClass.SOLIDBLOCK) {
                    column[y] = SolidBlock()
                } else {
                    column[y] = null
                }
            }
            // Reset back caches in DFS, because we have changed the column
            dfs = originalDFS
        }
        return column
    }

    override fun reset() {
        innerGenerator.reset()
        dfs.reset()
    }
}