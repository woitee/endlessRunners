package cz.woitee.endlessRunners.evolution.coevolution

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.random.Random

class CoevolverSerializationTest {
    @org.junit.jupiter.api.Test
    fun testSeeding() {
        val seed = Random.Default.nextLong()

        val coevolver = Coevolver(7, 30, 10, 10, seed)
        val coevolver2 = Coevolver(7, 30, 10, 10, seed)

        repeat(2) {
            fullIteration(coevolver)
            fullIteration(coevolver2)
        }

        assertSameResults(coevolver, coevolver2)
    }

    @org.junit.jupiter.api.Test
    fun testSerializing() {
        val seed = Random.Default.nextLong()

        val coevolver = Coevolver(7, 30, 10, 10, seed)
        fullIteration(coevolver)

        // Get copy of coevolver via serialization
        val byteArray = coevolver.toByteArray()
        val coevolver2 = Coevolver(7, 30, 10, 10)
        coevolver2.fromByteArray(byteArray)

        // They should be the same
        assertSameResults(coevolver, coevolver2)

        // They should continue being the same after being used
        fullIteration(coevolver)
        fullIteration(coevolver2)

        assertSameResults(coevolver, coevolver2)
    }

    private fun fullIteration(coevolver: Coevolver) {
        coevolver.evolveBlocks(5)
        coevolver.evolveController(5)
        coevolver.evolveDescription(5)
    }

    private fun assertSameResults(coevolver: Coevolver, coevolver2: Coevolver) {
        assertEquals(coevolver.blockEvoStates[0]!!.bestFitness, coevolver2.blockEvoStates[0]!!.bestFitness)
        assertEquals(coevolver.controllerEvoState!!.bestFitness, coevolver2.controllerEvoState!!.bestFitness)
        assertEquals(coevolver.gameDescriptionEvoState!!.bestFitness, coevolver2.gameDescriptionEvoState!!.bestFitness)
    }
}
