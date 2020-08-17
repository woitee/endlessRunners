package cz.woitee.endlessRunners.evolution.coevolution

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.random.Random

class CoevolverSerializationTest {
    @org.junit.jupiter.api.Test
    fun testSeeding() {
        val seed = Random.Default.nextLong()

        val coevolver = Coevolver(seed)
        val coevolver2 = Coevolver(seed)

        for (coevo in arrayOf(coevolver, coevolver2)) {
            coevo.evolveBlocks(5, 30, 4)
            coevo.evolveController(5, 10)
            coevo.evolveDescription(5, 10)
        }

        assertEquals(coevolver.blockPopulations[0].bestFitness, coevolver2.blockPopulations[0].bestFitness)
        assertEquals(coevolver.controllerPopulation!!.bestFitness, coevolver2.controllerPopulation!!.bestFitness)
        assertEquals(coevolver.gameDescriptionPopulation!!.bestFitness, coevolver2.gameDescriptionPopulation!!.bestFitness)
    }
}
