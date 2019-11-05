package cz.woitee.endlessRunners.evolution

import io.jenetics.BitChromosome
import io.jenetics.BitGene
import io.jenetics.Genotype
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import io.jenetics.engine.EvolutionStatistics
import java.util.function.Function

fun eval(genotype: Genotype<BitGene>): Int {
    return genotype.getChromosome()
            .`as`(BitChromosome::class.java)
            .bitCount()
}

fun main(args: Array<String>) {
    println("Hello world!")

    val factory = Genotype.of(BitChromosome.of(100, 0.5))
    val fitness = Function<Genotype<BitGene>, Int> { genotype -> eval(genotype) }

    val engine = Engine
            .builder(fitness, factory)
            .populationSize(1000)
            .build()

    val collector = EvolutionResult.toBestGenotype<BitGene, Int>()
    val statistics = EvolutionStatistics.ofNumber<Int>()

    val result = engine.stream()
            .limit(100)
            .peek({ result -> val best = result.bestPhenotype; println(best) })
            .peek(statistics)
            .collect(collector)

    println(result)
    println(statistics)
}
