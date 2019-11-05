package cz.woitee.endlessRunners.gameLaunchers

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.imitators.CanabalGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.CanabalLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController

/**
 * Runs the Canabal game, controlled by Keyboard.
 */
fun main(args: Array<String>) {
//    val playerController = KeyboardPlayerController()
    val playerController = DFSPlayerController()

    val game = Game(
            CanabalLevelGenerator(), playerController, GamePanelVisualizer(), gameDescription = CanabalGameDescription()
    )

    game.run()
}
