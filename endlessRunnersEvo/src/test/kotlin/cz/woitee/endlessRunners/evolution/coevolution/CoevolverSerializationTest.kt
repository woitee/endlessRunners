package cz.woitee.endlessRunners.evolution.coevolution

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.random.Random

class CoevolverSerializationTest {
    @org.junit.jupiter.api.Test
    fun testSeeding() {
        val seed = Random.Default.nextLong()

        val coevolver = Coevolver(seed)
        val coevolver2 = Coevolver(seed)

        repeat(2) {
            fullIteration(coevolver)
            fullIteration(coevolver2)
        }

        assertSameResults(coevolver, coevolver2)
    }

    @org.junit.jupiter.api.Test
    fun testSerializing() {
        val seed = Random.Default.nextLong()

        val coevolver = Coevolver(seed)
        fullIteration(coevolver)

        // Get copy of coevolver via serialization
        val byteArray = coevolver.toByteArray()
        val coevolver2 = Coevolver()
        coevolver2.fromByteArray(byteArray)

        // They should be the same
        assertSameResults(coevolver, coevolver2)

        // They should continue being the same after being used
        fullIteration(coevolver)
        fullIteration(coevolver2)

        assertSameResults(coevolver, coevolver2)
    }

    private fun fullIteration(coevolver: Coevolver) {
        coevolver.evolveBlocks(5, 30, 4)
        coevolver.evolveController(5, 10)
        coevolver.evolveDescription(5, 10)
    }

    private fun assertSameResults(coevolver: Coevolver, coevolver2: Coevolver) {
        assertEquals(coevolver.blockPopulations[0].bestFitness, coevolver2.blockPopulations[0].bestFitness)
        assertEquals(coevolver.controllerPopulation!!.bestFitness, coevolver2.controllerPopulation!!.bestFitness)
        assertEquals(coevolver.gameDescriptionPopulation!!.bestFitness, coevolver2.gameDescriptionPopulation!!.bestFitness)
    }
}
