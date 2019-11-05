package cz.woitee.endlessRunners.evolution.grandEvo

import cz.woitee.endlessRunners.utils.arrayList
import io.jenetics.*
import io.jenetics.engine.EvolutionResult
import io.jenetics.util.ISeq
import java.util.function.Function

/**
 * A utility class that can combine several genotypes and fitness function into one of each, and then
 * expand any results back to the original sizes.
 *
 * @param genotypes Sample genotypes to show the structure.
 */
class GenotypeCombiner(vararg genotypes: Genotype<DoubleGene>) {
    /**
     * A fitness component - fitness of one of the genotypes in this combination.
     */
    data class FitnessPart(val fitness: Function<Genotype<DoubleGene>, Double>, val weight: Double) {
        fun evaluate(genotype: Genotype<DoubleGene>): Double {
            return weight * fitness.apply(genotype)
        }
    }

    var factory: Genotype<DoubleGene>
        protected set
    protected val expansionRanges = ArrayList<IntRange>()
    protected val fitnessParts = ArrayList<FitnessPart>()

    val fitness: Function<Genotype<DoubleGene>, Double> = Function { it -> this.fitness(it) }

    init {
        val chromosomes = ArrayList<DoubleChromosome>()
        var currentIndex = 0
        for (genotype in genotypes) {
            expansionRanges.add(IntRange(currentIndex, currentIndex + genotype.length() - 1))
            currentIndex += genotype.length()
            for (chromosome in genotype) {
                val doubleChromosome = chromosome as DoubleChromosome
                chromosomes.add(doubleChromosome)
            }
        }
        factory = Genotype.of(chromosomes)
    }

    /**
     * Reexpands the single genotype back into individual components.
     */
    fun expand(genotype: Genotype<DoubleGene>): ArrayList<Genotype<DoubleGene>> {
        val genotypes = ArrayList<Genotype<DoubleGene>>()

        for (range in expansionRanges) {
            val chromosomes = ArrayList<Chromosome<DoubleGene>>()
            for (i in range) {
                chromosomes.add(genotype[i])
            }
            genotypes.add(Genotype.of(chromosomes))
        }
        return genotypes
    }

    /**
     * Sets the fitness portions to be used.
     */
    fun setFitnesses(vararg parts: FitnessPart) {
        fitnessParts.clear()
        fitnessParts.addAll(parts)
    }

    /**
     * Calculates fitness from the set parts.
     */
    fun fitness(genotype: Genotype<DoubleGene>): Double {
        var totalFitness = 0.0
        val genotypeParts = expand(genotype)

        for ((i, genotypePart) in genotypeParts.withIndex()) {
            val fitnessPart = fitnessParts[i]
            totalFitness += fitnessPart.evaluate(genotypePart)
        }

        return totalFitness
    }

    /**
     * Expands a whole evolution results gained at the end of a population.
     */
    fun expandEvolutionResult(evolutionResult: EvolutionResult<DoubleGene, Double>): ArrayList<EvolutionResult<DoubleGene, Double>> {
        val populations = arrayList(expansionRanges.count()) {
            ArrayList<Phenotype<DoubleGene, Double>>()
        }

        for (individual in evolutionResult.population) {
            for ((i, genotypePart) in expand(individual.genotype).withIndex()) {
                // Remember that using fitness decomposition is optional, so if it doesn't exist, replace fitnesses with constants
                val fitness = if (fitnessParts.count() > 0) fitnessParts[i].fitness else Function { individual.fitness }

                val phenotype = Phenotype.of(genotypePart, individual.generation, fitness) as Phenotype<DoubleGene, Double>
                populations[i].add(phenotype)
            }
        }

        val evolutionResults = ArrayList<EvolutionResult<DoubleGene, Double>>()
        for (i in populations.indices) {
            val population = ISeq.of(populations[i])
            val evoResult = EvolutionResult.of(
                    evolutionResult.optimize,
                    population,
                    evolutionResult.generation,
                    evolutionResult.totalGenerations,
                    evolutionResult.durations,
                    evolutionResult.killCount,
                    evolutionResult.invalidCount,
                    evolutionResult.alterCount
            )
            evolutionResults.add(evoResult)
        }

        return evolutionResults
    }
}
