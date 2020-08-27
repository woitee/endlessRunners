package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.CrouchGameDescription
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.utils.except

/**
 * Main method that launches an example evolution of 10 HeightBlocks for CrouchGame and then runs the game with a
 * Delayed Twin DFS player controller.
 */
fun main() {
    val gameDescription = CrouchGameDescription()
    val controllerFactory = { DFSPlayerController(DelayedTwinDFS(0.05)) }

    val runner = EvoBlockRunner(gameDescription, controllerFactory)
    val evolvedBlocks = runner.evolveMultipleBlocks(7)

    val controller = DFSPlayerController(DelayedTwinDFS(0.05))

    for ((i, block) in evolvedBlocks.withIndex()) {
        println()
        print(block)
        runner.printFitnessValuesOfBlock(block, evolvedBlocks.except(i))
        runner.demoRunBlock(block)
    }

    runner.runGameWithBlocks(evolvedBlocks, controller)
}
