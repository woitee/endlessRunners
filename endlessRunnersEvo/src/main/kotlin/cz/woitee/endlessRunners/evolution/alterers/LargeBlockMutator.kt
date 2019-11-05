package cz.woitee.endlessRunners.evolution.alterers

import cz.woitee.endlessRunners.geom.Vector2Int
import io.jenetics.*
import io.jenetics.util.*
import java.util.*

/**
 * Mutation that mutates not a single gene, but a 2D block of them (assuming the genotype represents a 2D matrix).
 * Internally uses the gene.mutate method to set a new value. Only mutates a pre-selected chromosome.
 *
 * @param probability Probability of doing mutation with centre in a specific gene
 * @param chromosomeIx The chromosome to mutate
 * @param size Size of the block to mutate (height = width)
 * @param blockDimensions The dimensions of the matrix we are mutating
 */
class LargeBlockMutator<G, C : Comparable<C>>(probability: Double, val chromosomeIx: Int, val size: Int, val blockDimensions: Vector2Int) :
        Mutator<G, C>(probability)
        where G : Gene<*, G>, G : Mean<G> {

    /**
     * Mutates a genotype.
     */
    override fun mutate(genotype: Genotype<G>, p: Double, random: Random): MutatorResult<Genotype<G>> {
        // The mutation here is set 100% for our desired chromosome and 0% to others
        val result = genotype.toSeq().mapIndexed { i, gt ->
            if (i == chromosomeIx) this.mutate(gt, p, random) else MutatorResult.of(gt)
        }
        return MutatorResult.of(Genotype.of(
                result.map { it.result }
        ), result.stream().mapToInt { it.mutations }.sum())
    }

    /**
     * Mutates one specific chromosome. Internally uses the gene.mutate method.
     */
    override fun mutate(chromosome: Chromosome<G>, p: Double, random: Random): MutatorResult<Chromosome<G>> {
        // squaring the probability, because we only work on one chromosome and we work on it always
        // dividing by size, because each application also affects nearby genes

        val P = io.jenetics.internal.math.probability.toInt(p * p)

        val geneList = chromosome.toMutableList()

        val negSize = - ((size - 1) / 2)
        val posSize = size / 2

        for (i in 0 until chromosome.count()) {
            if (random.nextInt() < P) {
                val gene = mutate(geneList[i], random)
                for (xOffset in negSize..posSize) {
                    for (yOffset in negSize..posSize) {
                        val loc = i + xOffset * blockDimensions.x + yOffset
                        if (loc in geneList.indices) {
                            geneList[loc] = gene
                        }
                    }
                }
            }
        }

        return MutatorResult.of(chromosome.newInstance(ISeq.of(geneList)))
    }
}
