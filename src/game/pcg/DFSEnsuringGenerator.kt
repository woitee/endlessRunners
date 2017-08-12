package game.pcg

import game.*
import game.gameObjects.GameObject
import game.gameObjects.*
import game.algorithms.DFS
import java.util.*

/**
 * Created by woitee on 22/07/2017.
 */

class DFSEnsuringGenerator(val innerGenerator: ILevelGenerator): ILevelGenerator {
    val dfs = DFS()
    override fun generateNextColumn(gameState: GameState): ArrayList<GameObject?> {
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
            gameState.removeFromGrid(column[y], oldGrid.width, y)
        }
        gameState.grid = oldGrid

        // return column if it is feasible, otherwise, copy SolidBlocks from last column
        if (!searchSucess){
            println("Failed, copying last")
            for (y in 0 .. column.lastIndex) {
                if (gameState.grid[gameState.grid.width - 2, y]?.isSolid == true) {
                    column[y] = SolidBlock()
                } else {
                    column[y] = null
                }
            }
            // Reset caches in DFS, because we have changed the column
            dfs.reset()
        }
        return column
    }

    override fun reset() {
        innerGenerator.reset()
        dfs.reset()
    }
}