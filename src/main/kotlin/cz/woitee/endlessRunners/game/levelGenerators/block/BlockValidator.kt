package cz.woitee.endlessRunners.game.levelGenerators.block

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.HeightBlocks
import cz.woitee.endlessRunners.game.algorithms.dfs.BasicDFS
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.FlatLevelGenerator
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController

class BlockValidator(val gameDescription: GameDescription, val dfs: BasicDFS) {
    val game = Game(FlatLevelGenerator(), NoActionPlayerController(), null, gameDescription = gameDescription)

    fun validate(block: HeightBlock): Boolean {
        val gameState = getBlockAsGameState(block)

        val result = dfs.searchForAction(gameState)
        return dfs.lastStats.success
    }

    protected fun getBlockAsGameState(block: HeightBlock): GameState {
        val gameState = GameState(game, null)
        gameState.grid.resizeWidth(block.width + 2)

        for (x in 0 until block.width) {
            for (y in 0 until block.height) {
                gameState.addToGrid(block.definition[x, y]?.makeCopy(), x, y)
            }
        }

        gameState.player.x = 0.0
        gameState.player.y = ((block.startHeight + 1) * BlockHeight).toDouble()

        val maxX = gameState.grid.width - 1
        for (y in 0 until HeightBlocks) {
            if (y !in block.endHeight + 1 .. block.endHeight + 2) {
                gameState.addToGrid(SolidBlock(), maxX, y)
                gameState.addToGrid(SolidBlock(), maxX - 1, y)
            }
        }

        return gameState
    }
}
