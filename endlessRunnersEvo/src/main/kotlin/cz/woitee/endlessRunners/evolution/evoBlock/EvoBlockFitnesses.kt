package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.BlockValidator
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.utils.ComputationStopper
import io.jenetics.Genotype
import io.jenetics.IntegerGene
import java.util.*
import java.util.function.Function
import kotlin.math.roundToInt

open class EvoBlockFitnesses(
    gameDescription: GameDescription,
    playerControllerFactory: () -> PlayerController,
    width: Int = 9,
    height: Int = 7,
    seed: Long = Random().nextLong(),
    allowHoles: Boolean = false,
    computationStopper: ComputationStopper = ComputationStopper()
) : EvoBlockMethods(gameDescription, playerControllerFactory, width, height, seed, allowHoles, computationStopper) {
    /**
     * Data class to hold elemental values used for fitness computation.
     */
    data class FitnessValues(
        val block: HeightBlock,
        val success: Boolean,
        val maxPlayerX: Double,
        val ruggedness: Int,
        val difficulty: Int,
        val contributingToMinority: Boolean = false,
        val numCustomObjects: Int = 0,
        val minimumDifferencesFromOthers: Int = 0,
        val minimumPlanDifferenceFromOthers: Int = 0,
        val plan: String = ""
    ) {
        override fun toString(): String {
            return "FitnessValues(success=$success, maxX=${maxPlayerX.toInt()}, ruggedness=$ruggedness, " +
                "difficulty=$difficulty, contribToMinority=$contributingToMinority, plan=$plan, " +
                "minPlanDiff=$minimumPlanDifferenceFromOthers)\n" +
                block.toString()
        }
    }

    /**
     * A copy constructor of EvoBlockFitnesses.
     */
    constructor(copied: EvoBlockFitnesses) : this(copied.gameDescription, copied.playerControllerFactory, copied.width, copied.height, copied.seed)

    val methods = EvoBlockMethods(gameDescription, playerControllerFactory, width, height, seed, allowHoles, computationStopper)

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
     * Calculate elementary fitness values for a given block, using the playerControllerFactory given.
     */
    fun getFitnessValues(block: HeightBlock): FitnessValues {
        val blockValidator = blockValidator
        val plan = blockValidator.getPlan(block)

        val blockIx = existingBlocks.indexOfFirst { it == block }
        val otherBlocks = if (blockIx == -1) existingBlocks else existingBlocks.filterIndexed { i, _ -> i != blockIx }
        val otherPlans = if (blockIx == -1) existingPlans else existingPlans.filterIndexed { i, _ -> i != blockIx }

        val numUpBlocks = otherBlocks.filter { it.goesUp }.count() ?: 0
        val numDownBlocks = otherBlocks.filter { it.goesDown }.count() ?: 0
        var numCustomObjects = 0
        for (x in 0 until block.width) {
            for (y in 0 until block.height) {
                if (block.definition[x, y]?.isCustomBlock == true) {
                    ++numCustomObjects
                }
            }
        }

        return FitnessValues(
            block,
            plan.success,
            plan.maxPlayerX,
            EvoBlockUtils.calculateRuggedness(block),
            EvoBlockUtils.calculateDifficulty(plan.actions),
            (block.goesUp && numUpBlocks < numDownBlocks) || (block.goesDown && numDownBlocks < numUpBlocks),
            numCustomObjects,
            otherBlocks.map { EvoBlockUtils.numDifferences(it, block) }.minOrNull() ?: 0,
            otherPlans.map { EvoBlockUtils.numDifferences(it, plan) }.minOrNull() ?: 0,
            plan.actions.filterNotNull().filter { it.interactionType != GameButton.InteractionType.RELEASE }.joinToString { it.gameButtonIx.toString() ?: "" }
        )
    }

    /**
     * First fitness function, considering only how far can they player get in a block.
     */
    val fitness1: java.util.function.Function<Genotype<IntegerGene>, Int> = Function { genotype: Genotype<IntegerGene> -> EvoBlockFitnesses(this).fitness1(genotype) }
    fun fitness1(genotype: Genotype<IntegerGene>): Int {
        val block = methods.genotype2block(genotype)
        return fitness1(block)
    }
    fun fitness1(heightBlock: HeightBlock): Int {
        val values = getFitnessValues(heightBlock)

        return values.maxPlayerX.roundToInt()
    }

    /**
     * A fitness function, considering how far the player gets and how rugged the block looks.
     */
    val fitness2: java.util.function.Function<Genotype<IntegerGene>, Int> = Function { genotype: Genotype<IntegerGene> -> EvoBlockFitnesses(this).fitness2(genotype) }
    fun fitness2(genotype: Genotype<IntegerGene>): Int {
        val block = methods.genotype2block(genotype)
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
    val fitness3: Function<Genotype<IntegerGene>, Int> = Function { genotype: Genotype<IntegerGene> -> EvoBlockFitnesses(this).fitness3(genotype) }
    fun fitness3(genotype: Genotype<IntegerGene>): Int {
        val block = methods.genotype2block(genotype)
        return fitness3(block)
    }
    fun fitness3(heightBlock: HeightBlock): Int {
        val values = getFitnessValues(heightBlock)

        return fitness3(values)
    }

    protected fun fitness3(values: FitnessValues): Int {
        val tooManyCustomPenalty = if (values.numCustomObjects <= 4) 0 else (values.numCustomObjects - 4) * 100
        val successBenefit = if (values.success) 2000 else 0

        return successBenefit +
            values.maxPlayerX.roundToInt() * 12 +
            Math.abs(values.difficulty - targetDifficulty) * (-100) +
            -values.ruggedness +
            -tooManyCustomPenalty
    }

    /**
     * The most advanced fitness function, also punishing the block for being too similiar to others.
     */
    val fitness4: Function<Genotype<IntegerGene>, Int> = Function { genotype: Genotype<IntegerGene> -> EvoBlockFitnesses(this).fitness4(genotype) }
    fun fitness4(genotype: Genotype<IntegerGene>): Int {
        val block = methods.genotype2block(genotype)
        return fitness4(block)
    }
    fun fitness4(heightBlock: HeightBlock): Int {
        val values = getFitnessValues(heightBlock)

        val fit3 = fitness3(values)

        val diffPenalty = if (values.minimumDifferencesFromOthers >= 3) 0 else 3 - values.minimumDifferencesFromOthers

        val differentPlanBenefit = if (values.minimumPlanDifferenceFromOthers > 0) 500 else 0

        return fit3 - diffPenalty * 200 + differentPlanBenefit + if (values.contributingToMinority) 500 else 0
    }
}
