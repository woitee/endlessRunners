package cz.woitee

import cz.woitee.game.Game
import cz.woitee.game.algorithms.DFS
import cz.woitee.game.algorithms.DelayedTwinDFS
import cz.woitee.game.descriptions.BitTripGameDescription
import cz.woitee.game.levelGenerators.SimpleLevelGenerator
import cz.woitee.game.levelGenerators.TestLevelGenerator
import cz.woitee.game.objects.SolidBlock
import cz.woitee.game.playerControllers.DFSPlayerController
import cz.woitee.gui.GamePanelVisualizer
import cz.woitee.utils.TimedThread

fun main(args: Array<String>) {
    visualizeDelayedTwinDFS()
}

fun visualizeDelayedTwinDFS(delayTime: Double = 0.25) {
    // Recommended breakpoint in DelayedTwinDFS::searchInternal
    val gameDescription = BitTripGameDescription()
    val levelGenerator = SimpleLevelGenerator()
    val delayedTwinDFS = DelayedTwinDFS(delayTime)
    val playerController = DFSPlayerController(delayedTwinDFS)
    val visualizer = GamePanelVisualizer()
    val game = Game(levelGenerator, playerController, visualizer,
            mode = Game.Mode.INTERACTIVE,
            gameDescription = gameDescription
    )
//    game.gameState.addToGrid(SolidBlock(), 20, 1)

    val currentStateVisualizer = GamePanelVisualizer("TwinDFS: Current State")
    val delayedStateVisualizer = GamePanelVisualizer("TwinDFS: Delayed State")
    currentStateVisualizer.frame.setLocation(700, 0)
    delayedStateVisualizer.frame.setLocation(700, 450)

    val currentStateThread = TimedThread({
        if (delayedTwinDFS.currentState != null) {
            currentStateVisualizer.update(delayedTwinDFS.currentState!!)
        }},
        75.0
    )
    val delayedStateThread = TimedThread({
        if (delayedTwinDFS.delayedState != null) {
            delayedStateVisualizer.update(delayedTwinDFS.delayedState!!)
        }},
        75.0
    )
    currentStateThread.start()
    delayedStateThread.start()

    game.gameState.tag = "Main"
    game.run()
}