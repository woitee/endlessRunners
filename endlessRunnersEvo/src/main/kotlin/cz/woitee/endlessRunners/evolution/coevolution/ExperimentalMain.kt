package cz.woitee.endlessRunners.evolution.coevolution

import cz.woitee.endlessRunners.evolution.EvoProgressAccumulator
import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.evoController.EvoControllerRunner
import cz.woitee.endlessRunners.evolution.evoGame.EvoGameRunner
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.descriptions.imitators.BitTriGameDescription
import cz.woitee.endlessRunners.game.descriptions.imitators.CanabalGameDescription
import cz.woitee.endlessRunners.game.descriptions.imitators.ChameleonGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.CanabalLevelGenerator
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.gameLaunchers.bitTriGameDefaultBlocks
import cz.woitee.endlessRunners.gameLaunchers.chameleonGameDefaultBlocks
import kotlin.random.Random

fun main() {
    val seed = Random.nextLong()
    println("Seed is $seed")

//    fullCoevolution(seed)
    checkFitnessOfKnownGames()
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
        val controllerGenerations = if (i == numIterations) 200L else 50L
        coevolver.evolveController(controllerGenerations, 50)
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

fun checkFitnessOfKnownGames() {
    val canabalGameDescription = CanabalGameDescription()
    val bitTriGameDescription = BitTriGameDescription()
    val chameleonGameDescription = ChameleonGameDescription()

    val bitTriBlocks = bitTriGameDefaultBlocks(bitTriGameDescription)
    val chameleonBlocks = chameleonGameDefaultBlocks(chameleonGameDescription)

    val canabalLevelGenerator = CanabalLevelGenerator()
    val bitTriBlockLevelGenerator = HeightBlockLevelGenerator(bitTriGameDescription, bitTriBlocks)
    val chameleonLevelGenerator = HeightBlockLevelGenerator(chameleonGameDescription, chameleonBlocks)

    val averagingNumber = 100
    var totalBitTri = 0.0
    var totalChameleon = 0.0

    repeat(averagingNumber) {
        // Evaluation
        val bitTriFitness = evaluateGame(bitTriGameDescription, bitTriBlocks)
        val chameleonFitness = evaluateGame(chameleonGameDescription, chameleonBlocks)
        totalBitTri += bitTriFitness
        totalChameleon += chameleonFitness
    }

    println("Bit Tri has fitness ${totalBitTri / averagingNumber}")
    println("Chameleon has fitness ${totalChameleon / averagingNumber}")
}

fun evaluateGame(gameDescription: GameDescription, blocks: ArrayList<HeightBlock>): Double {
    val evoProgressAccumulator = EvoProgressAccumulator()
    val controllerRunner = EvoControllerRunner(
            gameDescription,
            { HeightBlockLevelGenerator(gameDescription, blocks) },
            numGenerations = 300
//            evoProgressAccumulator = evoProgressAccumulator
    )

    val controller = controllerRunner.evolveController()

    val gameRunner = EvoGameRunner(
            { controller },
            { controller },
            blocks
    )

    val fitnessWithReasons = gameRunner.fitnessWithReasoning(gameDescription)
    return fitnessWithReasons.value
    println(fitnessWithReasons.reasoning)

    val blockRunner = EvoBlockRunner(
            gameDescription,
            { controller },
            300,
            100,
            evoProgressAccumulator = evoProgressAccumulator
    )

    val newBlocks = blockRunner.evolveMultipleBlocks(7)

    val newControllerRunner = EvoControllerRunner(
            gameDescription,
            { HeightBlockLevelGenerator(gameDescription, newBlocks) },
            numGenerations = 300,
            evoProgressAccumulator = evoProgressAccumulator
    )
    val newController = newControllerRunner.evolveController()

    val newGameRunner = EvoGameRunner(
            { newController },
            { newController },
            newBlocks,
            evoProgressAccumulator = evoProgressAccumulator
    )

    println(newGameRunner.fitnessWithReasoning(gameDescription).value)

    EvoBlockRunner(gameDescription, { newController }).runGameWithBlocks(newBlocks)

    return fitnessWithReasons.value
}
