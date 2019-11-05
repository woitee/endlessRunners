package cz.woitee.endlessRunners.evolution.evoGame

import cz.woitee.endlessRunners.evolution.evoGame.evolved.BestEvolvedGameDescriptions
import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController

/**
 * A simple main method running an evolution of GameDescriptions, using Delayed Twin DFS controllers.
 */
fun main(args: Array<String>) {
//    val gameDescription = TestingGameDescription()
//    val fitness = EvoGameRunner().fitness(gameDescription)
//
//    println("Fitness is $fitness")

    val runner = EvoGameRunner(
            { stopper -> DFSPlayerController(DelayedTwinDFS(0.1, computationStopper = stopper)) },
            { stopper -> DFSPlayerController(DelayedTwinDFS(0.1, computationStopper = stopper)) }
    )

    runner.runGame(BestEvolvedGameDescriptions.getGenotype(4))

//    runEvolution()
}

fun runEvolution() {
    val runner = EvoGameRunner(
            { stopper -> DFSPlayerController(DelayedTwinDFS(0.1, computationStopper = stopper)) },
            { stopper -> DFSPlayerController(DelayedTwinDFS(0.1, computationStopper = stopper)) }
    )

    val gameDescription = runner.evolveGame()

    println("Evolution finished, converting best genotype to a game")
    // Run the game

    println("Running game")
    runner.runGame(gameDescription)
}
