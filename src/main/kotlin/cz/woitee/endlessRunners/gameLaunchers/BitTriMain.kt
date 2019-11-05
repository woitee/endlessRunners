package cz.woitee.endlessRunners.gameLaunchers

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.imitators.BitTriGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.KeyboardPlayerController

/**
 * Runs the BitTri game, controlled by Keyboard.
 */
fun main(args: Array<String>) {
        val playerController = KeyboardPlayerController()
//    val playerController = DFSPlayerController()
    val gameDescription = BitTriGameDescription()

    val game = Game(
            HeightBlockLevelGenerator(gameDescription, bitTriGameDefaultBlocks(gameDescription)), playerController, GamePanelVisualizer(), gameDescription = gameDescription
    )

    game.run()
}
