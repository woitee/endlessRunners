package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.geom.Vector2Int
import cz.woitee.endlessRunners.utils.ComputationStopper
import io.jenetics.Genotype
import io.jenetics.IntegerChromosome
import io.jenetics.IntegerGene
import java.util.*
import kotlin.math.min

/**
 * A basic class containing fundamental methods for block evolution, most notably, different fitness functions.
 *
 * @param gameDescription GameDescription we evolve for
 * @param playerControllerFactory A factory returning player controllers that will be used to evaluate blocks
 * @param width Target width of the generated blocks
 * @param height Target height of the generated blocks
 * @param seed Seed for the random number generator
 * @param allowHoles Whether we allow blocks to have a hole at the bottom
 * @param computationStopper Computation stopper usable to stop the generation from the outside
 */
open class EvoBlockMethods(
    val gameDescription: GameDescription,
    val playerControllerFactory: () -> PlayerController,
    val width: Int = 9,
    val height: Int = 7,
    val seed: Long = Random().nextLong(),
    val allowHoles: Boolean = true,
    val computationStopper: ComputationStopper = ComputationStopper()
) {
    /**
     * Target shape of the generated blocks.
     */
    val blockDimension
        get() = Vector2Int(width, height)

    val maxPlayerHeight = 2

    /**
     * A copy constructor of EvoBlockMethods.
     */
    constructor(copied: EvoBlockMethods) : this(copied.gameDescription, copied.playerControllerFactory, copied.width, copied.height, copied.seed)

    /**
     * Returns a sample genotype of a block. Useful to start an evolution run.
     */
    fun sampleGenotype(): Genotype<IntegerGene> {
        val customBlocks = gameDescription.customObjects.count()
        val genotypeHeight = if (allowHoles) height else height - 1
        return Genotype.of(
            IntegerChromosome.of(0, maxPlayerHeight + 1, 2),
            IntegerChromosome.of(0, customBlocks + 1, (width - 1) * genotypeHeight)
        )
    }

    /**
     * Converts a genotype to a HeightBlock.
     *
     * @param genotype The genotype to convert
     * @param postProcess whether to additionally lower blocks with unnecessary height
     */
    fun genotype2block(genotype: Genotype<IntegerGene>, postProcess: Boolean = true): HeightBlock {
        val playerHeights = genotype[0]

        val block = HeightBlock(width, height)
        block.startHeight = playerHeights.getGene(0).allele
        block.endHeight = playerHeights.getGene(1).allele

        // If holes aren't allowed, add SolidBlock floor
        if (!allowHoles) {
            for (x in 0 until width) {
                block.definition[x, 0] = SolidBlock()
            }
        }

        // The last column in blocks doesn't count, so it is not encoded in genotype
        val genotypeWidth = width - 1
        for (y in 0 until height) {
            block.definition[block.width - 1, y] = if (y < block.endHeight) SolidBlock() else null
        }

        genotype[1].forEachIndexed { i, gene ->
            val x = i % genotypeWidth
            val y = i / genotypeWidth + (if (allowHoles) 0 else 1)

            block.definition[x, y] = when (gene.allele) {
                0 -> null
                1 -> SolidBlock()
                else -> CustomBlock(gene.allele - 2)
            }
        }

        // create space for player
        block.definition[0, block.startHeight] = SolidBlock()
        block.definition[0, block.startHeight + 1] = null
        block.definition[0, block.startHeight + 2] = null
        block.definition[block.width - 1, block.endHeight] = SolidBlock()
        block.definition[block.width - 1, block.endHeight + 1] = null
        block.definition[block.width - 1, block.endHeight + 2] = null

        if (postProcess) postprocessHeightBlock(block)

        return block
    }

    fun block2genotype(block: HeightBlock, padded: Boolean = true): Genotype<IntegerGene> {
        val localBlock = if (!padded) block else padBlock(block, Vector2Int(width, height))

        val geneSeq = ArrayList<Int>()

        val startHeight = if (allowHoles) 0 else 1

        for (y in startHeight until localBlock.height) {
            for (x in 0 until localBlock.width - 1) {
                geneSeq.add(
                    when (localBlock.definition[x, y]?.gameObjectClass) {
                        GameObjectClass.SOLIDBLOCK -> 1
                        GameObjectClass.CUSTOM0 -> 2
                        GameObjectClass.CUSTOM1 -> 3
                        GameObjectClass.CUSTOM2 -> 4
                        GameObjectClass.CUSTOM3 -> 5
                        else -> 0
                    }
                )
            }
        }

        val customObjects = gameDescription.customObjects.count()
        return Genotype.of(
            IntegerChromosome.of(
                IntegerGene.of(localBlock.startHeight, 0, maxPlayerHeight),
                IntegerGene.of(localBlock.endHeight, 0, maxPlayerHeight)
            ),
            IntegerChromosome.of(geneSeq.map { IntegerGene.of(it, 0, customObjects + 1) })
        )
    }

    fun padBlock(block: HeightBlock, targetDimensions: Vector2Int): HeightBlock {
        if (block.dimensions == targetDimensions) return block

        val newBlock = HeightBlock(targetDimensions.x, targetDimensions.y)

        for (y in 0 until block.height) {
            for (x in 0 until block.width) {
                newBlock.definition[x, y] = block.definition[x, y]?.makeCopy()
            }
            for (x in block.width until targetDimensions.x) {
                newBlock.definition[x, y] = block.definition[block.width - 1, y]?.makeCopy()
            }
        }

        newBlock.startHeight = block.startHeight
        newBlock.endHeight = block.endHeight

        return newBlock
    }

    /**
     * Postprocessing of the block - currently we reduce the height of "redundantly high" blocks - by searching for
     * the maximum height that is of solid block, and moving it down to 0 if it is more than 0.
     */
    fun postprocessHeightBlock(heightBlock: HeightBlock) {
        var maxSolidHeight = -1

        heightLoop@ for (height in 0..min(heightBlock.startHeight, heightBlock.endHeight)) {
            for (x in 0 until heightBlock.width) {
                if (heightBlock.definition[x, height]?.isSolid != true) {
                    continue@heightLoop
                }
            }
            maxSolidHeight = height
        }

        if (maxSolidHeight > 0) {
            heightBlock.definition.shiftY(-maxSolidHeight)
            heightBlock.startHeight -= maxSolidHeight
            heightBlock.endHeight -= maxSolidHeight
        }
    }
}
