package cz.woitee.endlessRunners.evolution.coevolution.evolved

import cz.woitee.endlessRunners.evolution.coevolution.CoevolutionRunner
import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockMethods
import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.evolution.evoGame.EvolvedGameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
import cz.woitee.endlessRunners.utils.JavaSerializationUtils
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.IntegerGene
import java.io.InputStreamReader

/**
 * A static class returning best results from our coevolution run.
 */
object CoevolvedTriples {
    /**
     * Gets one of the 20 resulting triplets (GameDescription, PlayerController, HeightBlocks).
     *
     * @param index From which run to return results. Should be in the range of 0 - 19.
     */
    fun get(index: Int): CoevolutionRunner.CoevolvedTriple {
        val reader = InputStreamReader(javaClass.getResourceAsStream("/best_of_run$index.txt"))
        val lines = reader.readLines()

        val gameDescription = EvolvedGameDescription(JavaSerializationUtils.unserializeFromString<Genotype<DoubleGene>>(lines[17])!!)
        val playerController = EvolvedPlayerController(JavaSerializationUtils.unserializeFromString<Genotype<DoubleGene>>(lines[15])!!)

        val evoBlockMethods = EvoBlockMethods(gameDescription, { NoActionPlayerController() })
        val blocks = ArrayList<HeightBlock>()
        for (i in 1..13) {
            if (i % 2 == 0) continue
            blocks.add(evoBlockMethods.genotype2block(JavaSerializationUtils.unserializeFromString<Genotype<IntegerGene>>(lines[i])!!))
        }

        return CoevolutionRunner.CoevolvedTriple(blocks, playerController, gameDescription)
    }
}
