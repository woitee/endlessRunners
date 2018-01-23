package cz.woitee

import cz.woitee.game.Game
import cz.woitee.game.algorithms.dfs.BasicDFS
import cz.woitee.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.game.descriptions.BitTripGameDescription
import cz.woitee.game.gui.DelayedTwinDFSVisualizer
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.game.levelGenerators.encapsulators.DelayedTwinDFSLevelGenerator
import java.lang.Thread.sleep

fun main(args: Array<String>) {
    visualizeDelayedTwinDFS()
}

fun visualizeDelayedTwinDFS(delayTime: Double = 0.25) {
    // Recommended breakpoint in DelayedTwinDFS::searchInternal
    val gameDescription = BitTripGameDescription()

    val levelGenerator = DelayedTwinDFSLevelGenerator(delayTime, SimpleLevelGenerator())
    val dfsOfPlayer = DelayedTwinDFS(delayTime)
    val dfsOfLevelGenerator = levelGenerator.dfsProvider as DelayedTwinDFS
    val playerController = DFSPlayerController(dfsOfPlayer, backupDFS = DelayedTwinDFS(delayTime))

    // Basic variants for testing parts
//    val levelGenerator = SimpleLevelGenerator()
//    val playerController = DFSPlayerController(BasicDFS())

    val visualizer = GamePanelVisualizer()
    val game = Game(levelGenerator, playerController, visualizer,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription,
            updateCallback = { game ->
                val genDelayedState = dfsOfLevelGenerator.buttonModel!!.delayedState
                val genCurrentState = dfsOfLevelGenerator.buttonModel!!.currentState
                val conDelayedState = dfsOfPlayer.buttonModel!!.delayedState
                val conCurrentState = dfsOfPlayer.buttonModel!!.currentState
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