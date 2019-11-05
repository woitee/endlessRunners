package cz.woitee.endlessRunners.gameLaunchers

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.imitators.ChameleonGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.KeyboardPlayerController

/**
 * Runs the Chameleon game with a keyboard controller.
 */
fun main(args: Array<String>) {
    val playerController = KeyboardPlayerController()
    val gameDescription = ChameleonGameDescription()
    val levelGenerator = HeightBlockLevelGenerator(gameDescription, chameleonGameDefaultBlocks(gameDescription))
    val game = Game(levelGenerator, playerController, GamePanelVisualizer(), gameDescription = gameDescription)
    game.run()
}
