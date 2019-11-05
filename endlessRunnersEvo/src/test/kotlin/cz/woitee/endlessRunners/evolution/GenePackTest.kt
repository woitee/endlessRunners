package cz.woitee.endlessRunners.evolution

import cz.woitee.endlessRunners.evolution.evoGame.EvolvedGameDescription
import io.jenetics.*
import io.jenetics.util.DoubleRange
import org.junit.jupiter.api.Assertions.*

class GenePackTest {
    @org.junit.jupiter.api.Test
    fun testIteration() {
        doTestIteration(2, 0)
        doTestIteration(3, 3)
        doTestIteration(4, 3)
        doTestIteration(5, 3)
        doTestIteration(6, 6)
        doTestIteration(7, 6)
        doTestIteration(8, 6)
        doTestIteration(9, 9)
    }

    fun doTestIteration(chromosomeLength: Int, assertLength: Int, genePackSize: Int = 3) {
        val chromosome = DoubleChromosome.of(DoubleRange.of(0.0, 4.0), chromosomeLength)
        val genePack = EvolvedGameDescription.GenePack(chromosome, genePackSize)

        val list = ArrayList<Double>()
        val itr = genePack
        while (itr.hasNext()) {
            val elem = itr.next()
            for (i in 0 until genePackSize) {
                list.add(elem.gene(i))
            }
        }
        assertEquals(assertLength, list.count())
    }
}
