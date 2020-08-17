package cz.woitee.endlessRunners

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.descriptions.imitators.BitTriGameDescription
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.KeyboardPlayerController
import cz.woitee.endlessRunners.game.playerControllers.wrappers.DisplayingWrapper
import cz.woitee.endlessRunners.gameLaunchers.bitTriGameDefaultBlocks

fun main(args: Array<String>) {
    // Just select the components! There is a lot of them prepared, but you can also make your own!

    // Choose how the game will be controlled
    val playerController = DisplayingWrapper(KeyboardPlayerController())

    // Choose what game do you want to play
    val gameDescription = BitTriGameDescription()

    // Select how the levels will be generated
    val levelGenerator = HeightBlockLevelGenerator(gameDescription, bitTriGameDefaultBlocks(gameDescription))

    // All set! Now create the Game object and run your game!

    val game = Game(levelGenerator, playerController, GamePanelVisualizer(), gameDescription = gameDescription)
    game.run()
}
