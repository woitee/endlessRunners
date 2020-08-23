package cz.woitee.endlessRunners.evolution.coevolution

import org.knowm.xchart.QuickChart
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChart
import kotlin.random.Random

fun main() {
    val seed = Random.nextLong()
    println("Seed is $seed")

    fullCoevolution(seed)
}

fun fullCoevolution(seed: Long) {
    val numIterations = 20
    val numBlocks = 7
    val coevolver = Coevolver(seed)
    val runner = CoevolutionRunner()

    for (i in 1..numIterations) {
        val paddedI = i.toString().padStart(2, '0')
        println("ITERATION $i")

        // =============== //
        // Evolving blocks //
        // =============== //

        print("evolving blocks ($numBlocks): ")
        coevolver.evolveBlocks(30, 30, numBlocks, true)
        coevolver.saveToFile("out/snapshots/$paddedI-1.snap")

        // =================== //
        // Evolving controller //
        // =================== //

        println("Evolving controller")
        coevolver.evolveController(200, 50)
        coevolver.saveToFile("out/snapshots/$paddedI-2.snap")
        println("Controller fitness: ${coevolver.controllerPopulation!!.bestFitness}")

        // ========================= //
        // Evolving game description //
        // ========================= //

        if (i == numIterations) {
            println("Not evolving game description in the last iteration, we want to end with best controller and blocks for a given game")
        } else {
            println(coevolver.currentBestGameDescription)
//            runner.runGame(coevolver.currentBestTriple(), 30.0)
            println("Evolving game description")
            coevolver.evolveDescription(50, 50).printReasoning()
            coevolver.saveToFile("out/snapshots/$paddedI-3.snap")
            println("Game Description fitness: ${coevolver.gameDescriptionPopulation!!.bestFitness}")
        }
    }

    println("Coevolution finished")
    runner.runGame(coevolver.currentBestTriple())
}
