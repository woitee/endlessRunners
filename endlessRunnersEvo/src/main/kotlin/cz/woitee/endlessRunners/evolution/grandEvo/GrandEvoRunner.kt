package cz.woitee.endlessRunners.evolution.grandEvo

import cz.woitee.endlessRunners.evolution.alterers.LargeBlockMutator
import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockMethods
import cz.woitee.endlessRunners.evolution.evoBlock.EvoBlockRunner
import cz.woitee.endlessRunners.evolution.evoController.EvoControllerRunner
import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlock
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.game.playerControllers.NoActionPlayerController
import cz.woitee.endlessRunners.game.playerControllers.PlayerController
import io.jenetics.*
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult
import java.util.function.Function

/**
 * A runner for combinations of multiple evolutions together.
 */
class GrandEvoRunner(val gameDescription: GameDescription) {

    /**
     * These methods serve to experiment and evolve an integer genetic problem using representation in encoded in doubles.
     */
    fun evolveBlockViaDoubles(playerControllerFactory: () -> PlayerController): HeightBlock {
        val evoResult = evolveBlockViaDoublesEvolutionResult(playerControllerFactory)

        val evoMethods = EvoBlockMethods(gameDescription, playerControllerFactory)
        return evoMethods.genotype2block(GenotypeConverter.doubleGenotype2intGenotype(evoResult.bestPhenotype.genotype, true))
    }
    fun evolveBlockViaDoublesEvolutionResult(playerControllerFactory: () -> PlayerController): EvolutionResult<DoubleGene, Double> {
        val evoMethods = EvoBlockMethods(gameDescription, playerControllerFactory)

        val genotype = GenotypeConverter.intGenotype2doubleGenotype(evoMethods.sampleGenotype())

        val fitness: Function<Genotype<DoubleGene>, Double> = Function {
            gt: Genotype<DoubleGene> ->
            evoMethods.fitness3(GenotypeConverter.doubleGenotype2intGenotype(gt, true)).toDouble()
        }

        val collector = EvolutionResult.toBestEvolutionResult<DoubleGene, Double>()

        val engine = Engine
            .builder(fitness, genotype)
            .populationSize(30)
            .offspringFraction(0.8)
            .maximalPhenotypeAge(1000)
            .survivorsSelector(EliteSelector())
            .offspringSelector(RouletteWheelSelector())
            .alterers(
                GaussianMutator<DoubleGene, Double>(1.0 / genotype.geneCount()),
                MultiPointCrossover(0.2),
                LargeBlockMutator<DoubleGene, Double>(0.03, 1, 3, evoMethods.blockDimension)
            )
            .build()

        return engine.stream()
            .limit(10)
            .collect(collector)
    }

    /**
     * Our first composition problem deals with evolving multiple blocks encoded in a single genotype.
     */
    fun evolveBlocks(playerControllerFactory: () -> PlayerController): ArrayList<HeightBlock> {
        val evoMethods = EvoBlockMethods(gameDescription, playerControllerFactory)

        val result = ArrayList<HeightBlock>()
        for (evoResult in evolveBlocksEvolutionResult(playerControllerFactory)) {
            result.add(evoMethods.genotype2block(GenotypeConverter.doubleGenotype2intGenotype(evoResult.bestPhenotype.genotype, true)))
        }
        return result
    }
    fun evolveBlocksEvolutionResult(playerControllerFactory: () -> PlayerController): ArrayList<EvolutionResult<DoubleGene, Double>> {
        val numBlocks = 7

        val evoMethods = EvoBlockMethods(gameDescription, playerControllerFactory)
        val genotypes = ArrayList<Genotype<DoubleGene>>()

        val fitnessParts = ArrayList<GenotypeCombiner.FitnessPart>()
        for (i in 1..numBlocks) {
            genotypes.add(GenotypeConverter.intGenotype2doubleGenotype(evoMethods.sampleGenotype()))
            fitnessParts.add(
                GenotypeCombiner.FitnessPart(
                    Function {
                        gt: Genotype<DoubleGene> ->
                        evoMethods.fitness3(GenotypeConverter.doubleGenotype2intGenotype(gt, true)).toDouble()
                    },
                    1.0
                )
            )
        }

        val combiner = GenotypeCombiner(*genotypes.toTypedArray())
        combiner.setFitnesses(*fitnessParts.toTypedArray())

        val engine = Engine
            .builder(combiner.fitness, combiner.factory)
            .populationSize(30)
            .offspringFraction(0.8)
            .maximalPhenotypeAge(1000)
            .survivorsSelector(EliteSelector())
            .offspringSelector(RouletteWheelSelector())
            .alterers(
                GaussianMutator<DoubleGene, Double>(1.0 / combiner.factory.geneCount()),
                MultiPointCrossover(0.2),
                LargeBlockMutator<DoubleGene, Double>(0.03, 1, 3, evoMethods.blockDimension)
            )
            .build()

        val collector = EvolutionResult.toBestEvolutionResult<DoubleGene, Double>()

        val result = engine.stream()
            .limit(3000)
            .collect(collector)

        return combiner.expandEvolutionResult(result)
    }

    /**
     * The second composition problem is evolving both blocks and the player controller simultaneously.
     */
    fun evolveBlocksAndPlayer(): Pair<ArrayList<HeightBlock>, EvolvedPlayerController> {
        val evoBlockRunner = EvoBlockRunner(gameDescription, { EvolvedPlayerController.sample })
        val blocks = ArrayList<HeightBlock>()
        blocks.addAll(evoBlockRunner.defaultBlocks)

        val evoResults = evolveBlocksAndPlayerEvolutionResult()
        for (evoResult in evoResults.subList(0, evoResults.count() - 1)) {
            val block = evoBlockRunner.genotype2block(GenotypeConverter.doubleGenotype2intGenotype(evoResult.bestPhenotype.genotype, true))
            blocks.add(block)
        }

        val playerController = EvolvedPlayerController(evoResults.last().bestPhenotype.genotype)
        return Pair(blocks, playerController)
    }
    fun evolveBlocksAndPlayerEvolutionResult(): ArrayList<EvolutionResult<DoubleGene, Double>> {
        val numBlocks = 7

        val evoMethods = EvoBlockMethods(gameDescription, { NoActionPlayerController() })
        val genotypes = ArrayList<Genotype<DoubleGene>>()

        for (i in 1..numBlocks) {
            genotypes.add(GenotypeConverter.intGenotype2doubleGenotype(evoMethods.sampleGenotype()))
        }

        genotypes.add(EvolvedPlayerController.sampleGenotype())

        val combiner = GenotypeCombiner(*genotypes.toTypedArray())

        fun fitness(genotype: Genotype<DoubleGene>): Double {
            val genotypeParts = combiner.expand(genotype)

            val evoBlockRunner = EvoBlockRunner(gameDescription, { EvolvedPlayerController(genotypeParts.last()) })
            val blocks = ArrayList<HeightBlock>()
            blocks.addAll(evoBlockRunner.defaultBlocks)

            for (i in 0..genotypeParts.count() - 2) {
                val blockGenotype = GenotypeConverter.doubleGenotype2intGenotype(genotypeParts[i], true)
                blocks.add(evoMethods.genotype2block(blockGenotype))
            }

            val avgBlockFitness = blocks.map { evoBlockRunner.fitness3(it) }.average()

            val evoControllerRunner = EvoControllerRunner(gameDescription, { HeightBlockLevelGenerator(gameDescription, blocks) })

            val controllerFitness = evoControllerRunner.fitness(EvolvedPlayerController(genotypeParts.last()))

            // EvoController is a minimization problem, so we invert it
            return 13000 + avgBlockFitness - controllerFitness
        }
        val fitness = Function<Genotype<DoubleGene>, Double> { fitness(it) }

        val engine = Engine
            .builder(fitness, combiner.factory)
            .populationSize(30)
            .offspringFraction(0.8)
            .maximalPhenotypeAge(1000)
            .survivorsSelector(EliteSelector())
            .offspringSelector(RouletteWheelSelector())
            .alterers(
                GaussianMutator<DoubleGene, Double>(1.0 / combiner.factory.geneCount()),
                MultiPointCrossover(0.2),
                LargeBlockMutator<DoubleGene, Double>(0.03, 1, 3, evoMethods.blockDimension)
            )
            .build()

        val collector = EvolutionResult.toBestEvolutionResult<DoubleGene, Double>()

        val result = engine.stream()
            .limit(3000)
            .peek { result -> println("${result.generation}: ${result.bestFitness}") }
            .collect(collector)

        return combiner.expandEvolutionResult(result)
    }
}
