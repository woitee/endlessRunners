package cz.woitee.endlessRunners.evolution.evoController

import cz.woitee.endlessRunners.game.*
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import cz.woitee.endlessRunners.geom.Vector2Int
import cz.woitee.endlessRunners.utils.arrayList
import io.jenetics.*
import kotlin.math.min

/**
 * A player controller representation of a genotype - a neural network.
 *
 * @param genotype The genotype of this player controller.
 */
open class EvolvedPlayerController(val genotype: Genotype<DoubleGene>) : PlayerController() {
    companion object {
        val inputWidth = 7
        val inputHeight = 7
        val numActions = 4
        val numCustomObjects = 3

        val sample
            get() = EvolvedPlayerController(sampleGenotype())

        /**
         * The input matrix is as follows (for size 5 x 5):
         *
         * .....
         * .....
         * P....
         * P....
         * .....
         *
         * the player is located all the way on the left and just below the middle (even if he could be placed in the middle).
         */

        /**
         * Sample genotype, usable for initiliazing evolution.
         */
        fun sampleGenotype(): Genotype<DoubleGene> {
            /** One chromosome for each custom_object->action action pair. */
            val list = ArrayList<DoubleChromosome>()
            for (i in 1..numActions * (numCustomObjects + 1)) {
                list.add(DoubleChromosome.of(-1.0, 1.0, inputWidth * inputHeight))
            }
            /** Last chromosome for weights from const-one input */
            list.add(DoubleChromosome.of(-1.0, 1.0, numActions))
            return Genotype.of(list)
        }
    }

    val weights = arrayList(numActions * (numCustomObjects + 1)) { Grid2D(inputWidth, inputHeight) { 0.0 } }
    val constOneWeights = arrayList(numActions) { 0.0 }

    init {
        // set weights
        for (chromosomeIx in 0..genotype.length() - 2) {
            val chromosome = genotype[chromosomeIx]
            chromosome.forEachIndexed { geneIx, gene ->
                weights[chromosomeIx][geneIx % inputWidth, geneIx / inputWidth] = gene.allele
            }
        }
        val lastChromosome = genotype.last()
        lastChromosome.forEachIndexed { geneIx, gene ->
            constOneWeights[geneIx] = gene.allele
        }
    }

    /**
     * Returns output of the network in a given GameState.
     */
    override fun onUpdate(gameState: GameState): GameButton.StateChange? {
        val playerLoc = gameState.gridLocation(gameState.player.x + (BlockWidth / 2), gameState.player.y + (BlockHeight / 2))
        val bottomLeft = Vector2Int(playerLoc.x, playerLoc.y - (inputHeight - 2) / 2)

        // Get activations of neurons in output layer
        val activations = ArrayList(constOneWeights)
        for (x in bottomLeft.x until bottomLeft.x + inputWidth) {
            for (y in bottomLeft.y until bottomLeft.y + inputHeight) {
                val block = gameState.grid.safeGet(x, y)
                if (block?.isSolid == true) {
                    for (actionIx in 0 until numActions) {
                        // the first set of weights is for solidblocks only
                        activations[actionIx] += weights[actionIx][x - bottomLeft.x, y - bottomLeft.y]
                    }
                }
                for (i in 0 until numCustomObjects) {
                    if (block?.gameObjectClass?.ord?.minus(GameObjectClass.CUSTOM0.ord) == i) {
                        for (actionIx in 0 until numActions) {
                            // the following sets of weights are for custom objects
                            activations[actionIx] += weights[(i + 1) * numActions + actionIx][x - bottomLeft.x, y - bottomLeft.y]
                        }
                    }
                }
            }
        }

        // Pick the highest activated and press it, or pick the lowest activated and release that
        var bestActionActivation = 0.0
        var bestButton: GameButton.StateChange? = null
//        gameState.buttons.forEachIndexed { buttonIx, button ->
        for (i in 0 until min(activations.size, gameState.buttons.size)) {
            val activation = activations[i]
            val button = gameState.buttons[i]

            if (activation > 0 && activation > bestActionActivation && !button.isPressed) {
                bestActionActivation = activation
                bestButton = button.hold
            }
            if (activation < 0 && -activation > bestActionActivation && button.isPressed) {
                bestActionActivation = -activation
                bestButton = button.release
            }
        }

        return bestButton
    }
}
