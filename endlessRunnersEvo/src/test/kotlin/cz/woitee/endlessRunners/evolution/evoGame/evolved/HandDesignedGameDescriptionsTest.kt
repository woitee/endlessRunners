package cz.woitee.endlessRunners.evolution.evoGame.evolved

import org.junit.jupiter.api.Assertions.*

internal class HandDesignedGameDescriptionsTest {
    @org.junit.jupiter.api.Test
    fun testBitTriValid() {
        assertTrue(bitTriEvolvedGameDescription().genotype.isValid)
    }

    @org.junit.jupiter.api.Test
    fun testChameleonValid() {
        assertTrue(chameleonEvolvedGameDescription().genotype.isValid)
    }
}
