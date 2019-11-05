package cz.woitee.endlessRunners

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.imitators.ChameleonGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.KeyboardPlayerController
import cz.woitee.endlessRunners.gameLaunchers.chameleonGameDefaultBlocks

fun main(args: Array<String>) {
    // Just select the components! There is a lot of them prepared, but you can also make your own!

    // Choose how the game will be controlled
    val playerController = KeyboardPlayerController()

    // Choose what game do you want to play
    val gameDescription = ChameleonGameDescription()

    // Select how the levels will be generated
    val levelGenerator = HeightBlockLevelGenerator(gameDescription, chameleonGameDefaultBlocks(gameDescription))

    // All set! Now create the Game object and run your game!

    val game = Game(levelGenerator, playerController, GamePanelVisualizer(), gameDescription = gameDescription)
    game.run()
}
