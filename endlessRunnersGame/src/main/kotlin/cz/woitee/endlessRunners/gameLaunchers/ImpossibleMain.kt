package cz.woitee.endlessRunners.gameLaunchers

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.imitators.ImpossibleGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.playerControllers.KeyboardPlayerController

/**
 * Runs the Impossible game with a keyboard controller.
 */
fun main(args: Array<String>) {
    val playerController = DFSPlayerController()
    val gameDescription = ImpossibleGameDescription()
    val levelGenerator = HeightBlockLevelGenerator(gameDescription, impossibleGameDefaultBlocks(gameDescription))
    val game = Game(levelGenerator, playerController, GamePanelVisualizer(), gameDescription = gameDescription)
    game.run()
}