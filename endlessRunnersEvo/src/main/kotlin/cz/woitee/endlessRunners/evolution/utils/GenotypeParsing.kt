package cz.woitee.endlessRunners.evolution.utils

import io.jenetics.DoubleChromosome
import io.jenetics.DoubleGene
import io.jenetics.Genotype

/**
 * A method that can parse a genotype of doubles from a string.
 */
fun genotypeFromPrintedString(str: String): Genotype<DoubleGene> {
    var stackDepth = 0
    var acc = StringBuilder()
    val chromosomes = ArrayList<DoubleChromosome>()
    var chromosomeBuilder = ArrayList<DoubleGene>()

    for (c in str) {
        if (c == '[') {
            stackDepth += 1
            acc.setLength(0)
        } else if (c == ']') {
            stackDepth -= 1
            val double = acc.toString().toDoubleOrNull()
            acc.setLength(0)
            if (double != null) {
                chromosomeBuilder.add(DoubleGene.of(double, 0.0, 1.0))
            } else if (chromosomeBuilder.count() > 0) {
                chromosomes.add(DoubleChromosome.of(*chromosomeBuilder.toTypedArray()))
                chromosomeBuilder.clear()
            }
        } else {
            acc.append(c)
        }
    }

    return Genotype.of(chromosomes)
}
