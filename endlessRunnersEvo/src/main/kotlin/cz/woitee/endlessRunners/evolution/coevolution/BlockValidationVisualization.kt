package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.evoGame.evolved.chameleonEvolvedGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.block.BlockValidator
import cz.woitee.endlessRunners.gameLaunchers.slowChameleonGameDefaultBlocks
import java.lang.Thread.sleep

fun main() {
    val gameDescription = chameleonEvolvedGameDescription()
    val blocks = slowChameleonGameDefaultBlocks(gameDescription)

    repeat(11) {
        blocks.removeAt(2)
    }

    val coevolver = Coevolver(blocks.size, 1, 30, 1)
    coevolver.seedWithGameDescription(gameDescription, 1.0)
    coevolver.seedWithBlocks(blocks, 1.0)

    val controller = coevolver.evolveController(1000)

    val blockValidator = BlockValidator(gameDescription, { controller })
    for (block in blocks) {
        val plan = blockValidator.getPlan(block)
        val gameState = blockValidator.getBlockAsGameState(block)

        val visualizer = GamePanelVisualizer()
        visualizer.update(gameState)

        for (action in plan.actions) {
            gameState.advanceByAction(action)
            if (gameState.isGameOver) break
            visualizer.update(gameState)
            sleep(20)
        }
        visualizer.stopGame(gameState)
        visualizer.dispose()
    }
}