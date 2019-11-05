package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.descriptions.imitators.BitTriGameDescription
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.utils.StopWatch

/**
 * A sample main method that runs an evolution of a single block and then shortly demos how the DFS plays through it.
 */
fun main(args: Array<String>) {
    val gameDescription = BitTriGameDescription()

    for (i in 1..10) {
        val stopWatch = StopWatch()
        stopWatch.start()
        val runner = EvoBlockRunner(gameDescription, { DFSPlayerController(DelayedTwinDFS(0.1)) })
//        val runner = EvoBlockRunner(gameDescription, { DFSPlayerController() })
        val block = runner.evolveBlock()

        println("Single block evolution took ${stopWatch.stop()} ms")
        runner.printFitnessValuesOfBlock(block)

        println(block.toString())

        runner.demoRunBlock(block)
    }
}
