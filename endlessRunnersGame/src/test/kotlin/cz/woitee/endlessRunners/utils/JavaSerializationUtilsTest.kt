package cz.woitee.endlessRunners.utils

import io.jenetics.*
import org.junit.jupiter.api.Assertions.*

class JavaSerializationUtilsTest {
    @org.junit.jupiter.api.Test
    fun copyGenotypeViaByteArray() {
        val genotype = Genotype.of(BitChromosome.of(2, 1.0))

        var serialized = JavaSerializationUtils.serialize(genotype)
        val unserialized = JavaSerializationUtils.unserialize(serialized) as Genotype<BitGene>

        assertTrue(unserialized[0, 0].bit)
        assertTrue(unserialized[0, 1].bit)
    }

    @org.junit.jupiter.api.Test
    fun copyGenotypeViaString() {
        val genotype = Genotype.of(BitChromosome.of(2, 1.0))

        var serialized = JavaSerializationUtils.serializeToString(genotype)
        val unserialized = JavaSerializationUtils.unserializeFromString(serialized) as Genotype<BitGene>

        assertTrue(unserialized[0, 0].bit)
        assertTrue(unserialized[0, 1].bit)
    }
}
