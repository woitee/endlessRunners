package cz.woitee.endlessRunners.evolution.evoGame

import cz.woitee.endlessRunners.game.algorithms.dfs.delayedTwin.DelayedTwinDFS
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
import cz.woitee.endlessRunners.utils.JavaSerializationUtils
import io.jenetics.DoubleGene
import io.jenetics.Phenotype
import io.jenetics.engine.EvolutionResult
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.FileReader

/**
 * Main method for replaying results of a specific run of the evolution.
 */
fun main() {
    val phenotype = findBestPhenotype("EvoGame_2018_06_24-13_21_27.csv")

    val runner = EvoGameRunner({ NoActionPlayerController() }, { NoActionPlayerController() })

    runner.runGame(phenotype.genotype, DFSPlayerController(DelayedTwinDFS(0.1)))
//    EvoGameRunner.runGame(phenotype.genotype, KeyboardPlayerController())
}

fun findBestPhenotype(filename: String): Phenotype<DoubleGene, Double> {
    val parser = CSVParser(FileReader("out/$filename"), CSVFormat.DEFAULT.withHeader("generation", "individuals", "fitnesses", "bestindividual", "bestfitness", "serialized"))

    // Skip header
    parser.iterator().next()

    var bestFitness: Double = Double.MIN_VALUE
    var bestPhenotype: Phenotype<DoubleGene, Double>? = null
    var bestGeneration = 0L
    for (record in parser.records) {
        val serialized = record[5]
        val population = JavaSerializationUtils.unserializeFromString<EvolutionResult<DoubleGene, Double>>(serialized)!!

        if (population.bestFitness > bestFitness) {
            bestPhenotype = population.bestPhenotype
            bestGeneration = population.generation
        }
    }

    println("Best genotype first found in generation $bestGeneration, with fitness $bestFitness")
    return bestPhenotype!!
}
