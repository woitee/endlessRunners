package cz.woitee.endlessRunners.evolution.evoBlock

import cz.woitee.endlessRunners.game.GameButton
import cz.woitee.endlessRunners.game.Grid2D
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.BlockValidator
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.playerControllers.DFSPlayerController
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import kotlin.math.max
import kotlin.math.min

/**
 * Utility functions usable when generating blocks.
 */
object EvoBlockUtils {
    /**
     * We calculate out of how many rectangles is the block composed of.
     */
    fun calculateRuggedness(block: HeightBlock): Int {
        val intBlock = Grid2D(block.width, block.height) { -1 }
        for (y in 0 until block.height) {
            for (x in 0 until block.width) {
                if (block.definition[x, y]?.isSolid == true) {
                    intBlock[x, y] = intVal(block.definition[x, y])
                }
            }
        }

        var rectangles = 0
        for (y in 0 until block.height) {
            for (x in 0 until block.width) {
                if (intBlock[x, y] != -1) {
                    ++rectangles
                    removeRectangle(intBlock, x, y)
                }
            }
        }
        return rectangles
    }

    private fun intVal(gameObject: GameObject?): Int {
        if (gameObject == null) return -1
        return gameObject.gameObjectClass.ord
    }
    private fun removeRectangle(intBlock: Grid2D<Int>, x: Int, y: Int) {
        var width = 0
        val intValue = intBlock[x, y]
        for (locX in x until intBlock.width) {
            if (intBlock[locX, y] != intValue) break
            intBlock[locX, y] = -1
            ++width
        }

        loop@ for (locY in y + 1 until intBlock.height) {
            for (locX in x until x + width) {
                if (intBlock[locX, locY] != intValue) break@loop
            }
            for (locX in x until x + width) {
                intBlock[locX, locY] = -1
            }
        }
    }

    /**
     * Calculate the difficulty of the block from a provided plan.
     */
    fun calculateDifficulty(plan: ArrayList<GameButton.StateChange?>): Int {
        var actionsNeeded = 0
        for (action in plan) {
            if (action != null && action.interactionType != GameButton.InteractionType.RELEASE) ++actionsNeeded
        }
        return actionsNeeded
    }

    /**
     * Calculate the number of differences between two blocks.
     *
     * @param blockA: First block
     * @param blockB: Second block
     */
    fun numDifferences(blockA: HeightBlock, blockB: HeightBlock): Int {
        val maxHeight = max(blockA.width, blockB.width)
        val maxWidth = max(blockA.height, blockB.height)

        var numDiffs = 0
        for (y in 0 until maxWidth) {
            for (x in 0 until maxHeight) {
                val objA = when {
                    blockA.definition.contains(x, y) -> blockA.definition[x, y]?.dumpChar ?: ' '
                    y <= blockA.startHeight || y <= blockA.endHeight -> '#'
                    else -> ' '
                }
                val objB = when {
                    blockB.definition.contains(x, y) -> blockB.definition[x, y]?.dumpChar ?: ' '
                    y <= blockB.startHeight || y <= blockB.endHeight -> '#'
                    else -> ' '
                }

                if (objA != objB) {
                    ++numDiffs
                }
            }
        }

        return numDiffs
    }

    /**
     * Number of differences between two plans. Only the sequence of the actions are considered, not the lengths of presses.
     *
     * @param planA: First plan
     * @param planB: Second plan
     */
    fun numDifferences(planA: BlockValidator.ActionPlan, planB: BlockValidator.ActionPlan): Int {
        val reducedA = planA.actions.filterNotNull()
        val reducedB = planB.actions.filterNotNull()

        val maxLen = max(reducedA.count(), reducedB.count())
        val minLen = min(reducedA.count(), reducedB.count())

        var numDiffs = maxLen - minLen
        for (i in 0 until minLen) {
            if (reducedA[i] != reducedB[i]) ++numDiffs
        }
        return numDiffs
    }

    /**
     * Print statistics of a block.
     *
     * @param gameDescription The encompassing GameDescription.
     * @param block Subject of the printing
     * @param playerControllerFactory a player controller factory for this print
     */
    fun printStatistics(gameDescription: GameDescription, block: HeightBlock, playerControllerFactory: () -> PlayerController = { DFSPlayerController() }) {
        val validator = BlockValidator(gameDescription, playerControllerFactory)
        val plan = validator.getPlan(block)
        val success = plan.success
        val difficulty = calculateDifficulty(plan.actions)
        val ruggedness = calculateRuggedness(block)

        println("BlockStats - Success: $success, ruggedness: $ruggedness, difficulty: $difficulty, reachedDepth: ${plan.maxPlayerX}")
    }
}
