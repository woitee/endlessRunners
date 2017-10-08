package cz.woitee

import cz.woitee.game.Game
import cz.woitee.game.algorithms.DelayedTwinDFS
import cz.woitee.game.descriptions.BitTripGameDescription
import cz.woitee.game.gui.DelayedTwinDFSVisualizer
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.levelGenerators.encapsulators.StateRemembering
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.game.gui.GamePanelVisualizer
import cz.woitee.utils.TimedThread

fun main(args: Array<String>) {
    visualizeDelayedTwinDFS()
}

fun visualizeDelayedTwinDFS(delayTime: Double = 0.25) {
    // Recommended breakpoint in DelayedTwinDFS::searchInternal
    val gameDescription = BitTripGameDescription()
    val levelGenerator = StateRemembering(SimpleLevelGenerator())
    val delayedTwinDFS = DelayedTwinDFS(delayTime)
    val playerController = DFSPlayerController(delayedTwinDFS)
    val visualizer = GamePanelVisualizer()
    val game = Game(levelGenerator, playerController, visualizer,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )
//    game.gameState.addToGrid(SolidBlock(), 20, 1)

    val delayedTwinDFSVisualizer = DelayedTwinDFSVisualizer(delayedTwinDFS)
    delayedTwinDFSVisualizer.start()

    game.gameState.tag = "Main"
    game.run()
    delayedTwinDFSVisualizer.stop()
}