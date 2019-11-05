package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.BlockValidator
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.geom.Vector2Int
import cz.woitee.endlessRunners.utils.ComputationStopper
import io.jenetics.Genotype
import io.jenetics.IntegerChromosome
import io.jenetics.IntegerGene
import java.util.*
import java.util.function.Function
import kotlin.math.min
import kotlin.math.roundToInt

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
    val width: Int = 7,
    val height: Int = 7,
    val seed: Long = Random().nextLong(),
    val allowHoles: Boolean = false,
    val computationStopper: ComputationStopper = ComputationStopper()
) {
    /**
     * A copy constructor of EvoBlockMethods.
     */
    constructor(copied: EvoBlockMethods) : this(copied.gameDescription, copied.playerControllerFactory, copied.width, copied.height, copied.seed)

    /**
     * Data class to hold elemental values used for fitness computation.
     */
    data class FitnessValues(
        val success: Boolean,
        val maxPlayerX: Double,
        val ruggedness: Int,
        val difficulty: Int,
        val contributingToMinority: Boolean = false,
        val numCustomObjects: Int = 0,
        val minimumDifferencesFromOthers: Int = 0,
        val minimumPlanDifferenceFromOthers: Int = 0
    )

    /**
     * Calculate elementary fitness values for a given block, using the playerControllerFactory given.
     */
    fun getFitnessValues(block: HeightBlock, otherBlocks: List<HeightBlock>? = null, otherPlans: List<BlockValidator.ActionPlan>? = null): FitnessValues {
        val blockValidator = blockValidator
        val plan = blockValidator.getPlan(block)

        val numUpBlocks = otherBlocks?.filter { isUpBlock(it) }?.count() ?: 0
        val numDownBlocks = otherBlocks?.filter { isDownBlock(it) }?.count() ?: 0
        var numCustomObjects = 0
        for (x in 0 until block.width) {
            for (y in 0 until block.height) {
                if (block.definition[x, y]?.isCustomBlock == true) {
                    ++numCustomObjects
                }
            }
        }

        return FitnessValues(
                plan.success,
                plan.maxPlayerX,
                EvoBlockUtils.calculateRuggedness(block),
                EvoBlockUtils.calculateDifficulty(plan.actions),
                (isUpBlock(block) && numUpBlocks < numDownBlocks) || (isDownBlock(block) && numDownBlocks < numUpBlocks),
                numCustomObjects,
                otherBlocks?.map { EvoBlockUtils.numDifferences(it, block) }?.min() ?: 0,
                otherPlans?.map { EvoBlockUtils.numDifferences(it, plan) }?.min() ?: 0
        )
    }

    /**
     * Target shape of the generated blocks.
     */
    val blockDimension
        get() = Vector2Int(width, height)

    /**
     * A block validator used to evaluate the playability and difficulty of blocks.
     */
    val blockValidator: BlockValidator = BlockValidator(gameDescription, playerControllerFactory, seed = seed)

    /**
     * Other, already generated blocks. Some fitness take them into account and try not to generate similiar blocks.
     */
    // Other blocks that already exist. Plans for these blocks get generated every time they are set into var existingPlans
    var existingBlocks: List<HeightBlock> = ArrayList()
            set(value) {
                field = value
                existingPlans = value.map(blockValidator::getPlan)
            }
    /**
     * Plans (sequences of actions) of how to best behave in existingBlocks.
     */
    var existingPlans: List<BlockValidator.ActionPlan> = ArrayList()
        protected set

    /**
     * Desired difficulty of the blocks.
     */
    val targetDifficulty = 1

    /**
     * First fitness function, considering only how far can they player get in a block.
     */
    val fitness1: Function<Genotype<IntegerGene>, Int> = Function { genotype: Genotype<IntegerGene> -> EvoBlockMethods(this).fitness1(genotype) }
    fun fitness1(genotype: Genotype<IntegerGene>): Int {
        val block = genotype2block(genotype)
        return fitness1(block)
    }
    fun fitness1(heightBlock: HeightBlock): Int {
        val values = getFitnessValues(heightBlock)

        return values.maxPlayerX.roundToInt()
    }

    /**
     * A fitness function, considering how far the player gets and how rugged the block looks.
     */
    val fitness2: Function<Genotype<IntegerGene>, Int> = Function { genotype: Genotype<IntegerGene> -> EvoBlockMethods(this).fitness2(genotype) }
    fun fitness2(genotype: Genotype<IntegerGene>): Int {
        val block = genotype2block(genotype)
        return fitness2(block)
    }
    fun fitness2(heightBlock: HeightBlock): Int {
        val values = getFitnessValues(heightBlock)

        return values.maxPlayerX.roundToInt() * 12 - values.ruggedness
    }

    /**
     * Another fitness function, additionaly caring about the difficulty (number of actions used) of the block.
     */
    // Fitness 3 deals with winnability, smoothness and difficulty (required number of actions)
    val fitness3: Function<Genotype<IntegerGene>, Int> = Function { genotype: Genotype<IntegerGene> -> EvoBlockMethods(this).fitness3(genotype) }
    fun fitness3(genotype: Genotype<IntegerGene>): Int {
        val block = genotype2block(genotype)
        return fitness3(block)
    }
    fun fitness3(heightBlock: HeightBlock): Int {
        val values = getFitnessValues(heightBlock)

        return fitness3(values)
    }
    protected fun fitness3(values: FitnessValues): Int {
        val tooManyCustomPenalty = if (values.numCustomObjects <= 4) 0 else (values.numCustomObjects - 4) * 100

        return values.maxPlayerX.roundToInt() * 12 - Math.abs(values.difficulty - targetDifficulty) * 100 - values.ruggedness - tooManyCustomPenalty
    }

    /**
     * The most advanced fitness function, also punishing the block for being too similiar to others.
     */
    val fitness4: Function<Genotype<IntegerGene>, Int> = Function { genotype: Genotype<IntegerGene> -> EvoBlockMethods(this).fitness4(genotype) }
    fun fitness4(genotype: Genotype<IntegerGene>): Int {
        val block = genotype2block(genotype)
        return fitness4(block)
    }
    fun fitness4(heightBlock: HeightBlock): Int {
        val values = getFitnessValues(heightBlock)

        val fit3 = fitness3(values)

        val diffPenalty = if (values.minimumDifferencesFromOthers >= 3) 0 else 3 - values.minimumDifferencesFromOthers

        return fit3 - diffPenalty * 200 + if (values.contributingToMinority) 500 else 0
    }

    fun isUpBlock(heightBlock: HeightBlock): Boolean {
        return heightBlock.startHeight < heightBlock.endHeight
    }
    fun isDownBlock(heightBlock: HeightBlock): Boolean {
        return heightBlock.startHeight > heightBlock.endHeight
    }

    /**
     * Returns a sample genotype of a block. Useful to start an evolution run.
     */
    fun sampleGenotype(): Genotype<IntegerGene> {
        val customBlocks = gameDescription.customObjects.count()
        return Genotype.of(
                IntegerChromosome.of(0, 3, 2),
                IntegerChromosome.of(0, customBlocks + 1, width * (height - 1))
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

        if (!allowHoles) {
            for (x in 0 until width) {
                block.definition[x, 0] = SolidBlock()
            }
        }

        genotype[1].forEachIndexed { i, gene ->
            val x = i % width
            val y = i / width + 1

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
