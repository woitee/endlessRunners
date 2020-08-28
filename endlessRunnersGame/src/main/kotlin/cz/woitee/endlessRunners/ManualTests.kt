package cz.woitee.endlessRunners

import cz.woitee.endlessRunners.game.Game
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.imitators.BitTriGameDescription
import cz.woitee.endlessRunners.game.gui.DelayedTwinDFSVisualizer
import cz.woitee.endlessRunners.game.gui.GamePanelVisualizer
import cz.woitee.endlessRunners.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.encapsulators.DelayedTwinDFSLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController

/**
 * Manually launching test, to see if they still behave correctly.
 */
fun main(args: Array<String>) {
    visualizeDelayedTwinDFS()
}

/**
 * A visualization of Delayed Twin DFS.
 */
fun visualizeDelayedTwinDFS(delayTime: Double = 0.25) {
    // Recommended breakpoint in DelayedTwinDFS::searchInternal
    val gameDescription = BitTriGameDescription()

    val levelGenerator = DelayedTwinDFSLevelGenerator(delayTime, SimpleLevelGenerator())
    val dfsOfPlayer = DelayedTwinDFS(delayTime)
    val dfsOfLevelGenerator = levelGenerator.dfsProvider as DelayedTwinDFS
    val playerController = DFSPlayerController(dfsOfPlayer, backupDFS = DelayedTwinDFS(delayTime))

    // Basic variants for testing parts
//    val levelGenerator = SimpleLevelGenerator()
//    val playerController = DFSPlayerController(BasicDFS())

    val visualizer = GamePanelVisualizer()
    val game = Game(
        levelGenerator,
        playerController,
        visualizer,
        mode = Game.Mode.INTERACTIVE,
        gameDescription = gameDescription,
        updateCallback = { game ->
            val genDelayedState = dfsOfLevelGenerator.buttonModel.delayedState
            val genCurrentState = dfsOfLevelGenerator.buttonModel.currentState
            val conDelayedState = dfsOfPlayer.buttonModel.delayedState
            val conCurrentState = dfsOfPlayer.buttonModel.currentState
            val gameState = game.gameState

            assert(genDelayedState.player.x == gameState.player.x)
            assert(genDelayedState.player.y == gameState.player.y)
            assert(conDelayedState.player.x == game.gameState.player.x)
            assert(conDelayedState.player.y == game.gameState.player.y)
            if (genDelayedState.player.x != genCurrentState.player.x) {
                assert(genCurrentState.player.x == conCurrentState.player.x)
            }
        }
    )
//    game.random.setSeed(1234)
//    game.currentState.addToGrid(SolidBlock(), 20, 1)

    val levelGeneratorDFSVisualizer = DelayedTwinDFSVisualizer(dfsOfLevelGenerator, 700, 400)
    levelGeneratorDFSVisualizer.start()
    val playerDFSVisualizer = DelayedTwinDFSVisualizer(dfsOfPlayer)
    playerDFSVisualizer.start()

    val backupDFSVisualizer = DelayedTwinDFSVisualizer(dfsOfPlayer, 0, 500)
    backupDFSVisualizer.start()

    game.gameState.tag = "Main"
    game.run()
//    delayedTwinDFSVisualizer.dispose()
    levelGeneratorDFSVisualizer.dispose()
}
