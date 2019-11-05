package cz.woitee.endlessRunners.evolution.utils

import io.jenetics.Gene
import io.jenetics.Genotype
import io.jenetics.Phenotype
import io.jenetics.engine.Engine
import io.jenetics.internal.util.Concurrency
import io.jenetics.util.ISeq
import io.jenetics.util.Seq
import java.util.*
import java.util.concurrent.Executor

/**
 * A custom implementation of concurrent evaluator (mostly rewriting io.jenetics.engine.ConcurrentEvaluator to Kotlin),
 * which was necessary, for the original has only internal visibility.
 *
 *
 * Additionally we deal with some of the issues when using jenetics. First, we provide a simple option to reevaluate all,
 * even surviving individuals in each generation. Secondly, we can distribute seeds consistently to fitness evaluations,
 * such that we have reproducible results.
 */

class MyConcurrentEvaluator<G : Gene<*, G>, C : Comparable<C>>(
    protected val executor: Executor,
    protected val alwaysEvaluate: Boolean = false,
    protected val seed: Long? = null
) : Engine.Evaluator<G, C> {

    protected val random = if (seed != null) Random(seed) else Random()
    protected val seedMap = HashMap<Int, Long>()

    /**
     * Returns a seed a specific genotype should be evaluated with.
     */
    fun seedForGenotype(genotype: Genotype<G>): Long {
        return seedMap[System.identityHashCode(genotype)]!!
    }

    /**
     * Returns whether all of the individuals are already evaluated in a population.
     */
    private fun isEverybodyEvaluated(population: Iterable<Phenotype<G, C>>): Boolean {
        for (phenotype in population) {
            if (!phenotype.isEvaluated) return false
        }
        return true
    }

    /**
     * Evaluate implementation, possibly reevaluating all individuals in a population.
     */
    override fun evaluate(population: Seq<Phenotype<G, C>>): ISeq<Phenotype<G, C>> {
        seedMap.clear()
        population.forEach {
            val genotypeId = System.identityHashCode(it.genotype)
            seedMap[genotypeId] = random.nextLong()
        }

        var toEvalute = population.stream()

        // Evaluation happens twice per evolution step in jenetics
        // But we should do the "always evaluation" only once, since it would be wasteful otherwise
        val shouldReevaluateAll = alwaysEvaluate && isEverybodyEvaluated(population)

        if (shouldReevaluateAll) {
            // The only way to un-evaluate a phenotype is to create a new one
            toEvalute = toEvalute.map { pt -> pt.newInstance(pt.genotype); }
        } else {
            toEvalute = toEvalute.filter { pt -> !pt.isEvaluated }
        }

        val seq = toEvalute.collect(ISeq.toISeq())

        executeAll(seq)

        return if (shouldReevaluateAll) { seq } else { population.asISeq() }
    }

    /**
     * Execute the individual evaluations.
     */
    protected fun executeAll(seq: Seq<Phenotype<G, C>>) {
        if (seq.isEmpty) return
        Concurrency.with(executor).execute(seq)
    }
}
