package cz.woitee.endlessRunners.game.levelGenerators.block

import cz.woitee.endlessRunners.game.*
import cz.woitee.endlessRunners.game.algorithms.dfs.AbstractDFS
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import java.util.*
import kotlin.math.abs

/**
 * A validator of HeightBlocks, checking their playability and difficulty.
 *
 * @param gameDescription The GameDescription in which the HeightBlocks exist.
 * @param playerControllerFactory Provider of players to use for assessing blocks.
 * @param seed seed for a random number generator in the game
 */
class BlockValidator(val gameDescription: GameDescription, val playerControllerFactory: () -> PlayerController, seed: Long = Random().nextLong()) {
    /**
     * Data class representing the result of a validation.
     */
    data class ActionPlan(val actions: ArrayList<GameButton.StateChange?>, val success: Boolean, val maxPlayerX: Double)
    val game = Game(FlatLevelGenerator(), NoActionPlayerController(), null, gameDescription = gameDescription, seed = seed)

    /**
     * Returns whether the block is playable (by the given player)
     */
    fun validate(block: HeightBlock): Boolean {
        return getPlan(block).success
    }

    /**
     * Returns a plan of actions if the block is playable.
     */
    fun getPlan(block: HeightBlock): ActionPlan {
        // If the player is DFS, get plan directly
        val playerController = playerControllerFactory()
//        if (playerController is DFSPlayerController) {
//            return getPlanFromDFS(block, playerController.dfs)
//        }

        var maxPlayerX = 0.0
        val actionList = ArrayList<GameButton.StateChange?>()
        val gameState = getBlockAsGameState(block)

//        val visualizer = GamePanelVisualizer(debugging = true)
//        visualizer.update(gameState)

        while (!gameState.isGameOver && gameState.player.nextX() <= block.width * BlockWidth) {
            val action = playerController.onUpdate(gameState)
            actionList.add(action)
            gameState.advanceUndoableByAction(action)
//            visualizer.update(gameState)
            if (gameState.player.x > maxPlayerX) maxPlayerX = gameState.player.x
        }

        val endHeightDiff = abs(gameState.player.y - 24 * (1 + block.endHeight))
        val success = !gameState.isGameOver && endHeightDiff < 4.0
        return ActionPlan(actionList, success, maxPlayerX)
    }

    /**
     * Utilizes DFS directly to get a plan, without needing of stepping by a PlayerController.
     */
    fun getPlanFromDFS(block: HeightBlock, dfs: AbstractDFS): ActionPlan {
        val gameState = getBlockAsGameState(block)

        dfs.init(gameState)
        val plan = dfs.searchForPlan(gameState)
        return ActionPlan(plan, dfs.lastStats.success, dfs.lastStats.maxPlayerX)
    }

    /**
     * Creates a GameState from a block.
     */
    fun getBlockAsGameState(block: HeightBlock, game: Game = this.game): GameState {
        val gameState = GameState(game, null)
        gameState.grid.resizeWidth(block.width + 2)
        val maxX = gameState.grid.width - 1
        val maxY = gameState.grid.height - 1

        for (x in 0 until block.width) {
            // Remove default block at height 0
            gameState.remove(gameState.grid[x, 0])
            for (y in 0 until block.height) {
                gameState.addToGrid(block.definition[x, y]?.makeCopy(), x, y)
            }
            gameState.addToGrid(SolidBlock(), x, maxY)
        }

        gameState.player.x = 0.0
        gameState.player.y = ((block.startHeight + 1) * BlockHeight).toDouble()

        for (x in maxX - 1..maxX) {
            gameState.addToGrid(block.definition[block.width - 1, block.endHeight]?.makeCopy(), x, block.endHeight)

//            for (y in block.endHeight + 3 until gameState.grid.height) {
//                gameState.addToGrid(SolidBlock(), x, y)
//            }
        }

        return gameState
    }
}
