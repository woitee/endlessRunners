package cz.woitee.endlessRunners.evolution.grandEvo

import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockMethods
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
import io.jenetics.IntegerChromosome
import org.junit.jupiter.api.Assertions.*

class GenotypeConverterTest {
    @org.junit.jupiter.api.Test
    fun genotypeExactConversions() {
        val genotype = EvoBlockMethods(GameDescription(), { NoActionPlayerController() }).sampleGenotype()

        val converted = GenotypeConverter.intGenotype2doubleGenotype(genotype, true)
        val recovered = GenotypeConverter.doubleGenotype2intGenotype(converted, true)

        for ((i, chromosome) in genotype.withIndex()) {
            val originalChromosome = chromosome as IntegerChromosome
            val recoveredChromosome = recovered[i] as IntegerChromosome

            assertEquals(chromosome.length(), recoveredChromosome.length())
            assertEquals(originalChromosome.min, recoveredChromosome.min)
            assertEquals(originalChromosome.max, recoveredChromosome.max)

            for ((j, gene) in chromosome.withIndex()) {
                val recoveredGene = recoveredChromosome.getGene(j)
                assertEquals(gene.allele, recoveredGene.allele)
            }
        }
    }

    @org.junit.jupiter.api.Test
    fun genotypeLengthRangeConversions() {
        val genotype = EvoBlockMethods(GameDescription(), { NoActionPlayerController() }).sampleGenotype()

        val converted = GenotypeConverter.intGenotype2doubleGenotype(genotype, false)
        val recovered = GenotypeConverter.doubleGenotype2intGenotype(converted, false)

        for ((i, chromosome) in genotype.withIndex()) {
            val originalChromosome = chromosome as IntegerChromosome
            val recoveredChromosome = recovered[i] as IntegerChromosome

            assertEquals(originalChromosome.min, recoveredChromosome.min)
            assertEquals(originalChromosome.max, recoveredChromosome.max)
            assertEquals(originalChromosome.lengthRange(), recoveredChromosome.lengthRange())
        }
    }
}
