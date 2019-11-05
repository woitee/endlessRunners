package cz.woitee.endlessRunners.evolution.grandEvo

import io.jenetics.*

/**
 * Conversion utils between integer and double genotypes.
 */
object GenotypeConverter {
    const val ALMOST_ONE: Double = 0.9999

    fun intGenotype2doubleGenotype(genotype: Genotype<IntegerGene>, exactLengthAndValues: Boolean = false): Genotype<DoubleGene> {
        val chromosomes = ArrayList<DoubleChromosome>()
        for (chromosome in genotype) {
            val integerChromosome = (chromosome as IntegerChromosome)
            if (exactLengthAndValues) {
                val doubleGenes = integerChromosome.map { intGene -> DoubleGene.of(intGene.doubleValue(), intGene.min.toDouble(), intGene.max.toDouble() + ALMOST_ONE) }
                chromosomes.add(DoubleChromosome.of(
                        *doubleGenes.toTypedArray()
                ))
            } else {
                chromosomes.add(DoubleChromosome.of(
                        // the maximum is larger to provide the same length on the number axis
                        integerChromosome.min.toDouble(), integerChromosome.max.toDouble() + ALMOST_ONE, integerChromosome.lengthRange()
                ))
            }
        }
        return Genotype.of(chromosomes)
    }

    fun doubleGenotype2intGenotype(genotype: Genotype<DoubleGene>, exactLengthAndValues: Boolean = false): Genotype<IntegerGene> {
        val chromosomes = ArrayList<IntegerChromosome>()
        for (chromosome in genotype) {
            val doubleChromosome = (chromosome as DoubleChromosome)
            if (exactLengthAndValues) {
                val intGenes = doubleChromosome.map { doubleGene -> IntegerGene.of(Math.floor(doubleGene.allele).toInt(), doubleGene.min.toInt(), (doubleChromosome.max - ALMOST_ONE).toInt()) }
                chromosomes.add(IntegerChromosome.of(
                        *intGenes.toTypedArray()
                ))
            } else {
                chromosomes.add(IntegerChromosome.of(
                        doubleChromosome.min.toInt(), (doubleChromosome.max - ALMOST_ONE).toInt(), doubleChromosome.lengthRange()
                ))
            }
        }
        return Genotype.of(chromosomes)
    }
}
