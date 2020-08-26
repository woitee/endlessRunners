package cz.woitee.endlessRunners.evolution.evoGame.evolved

import cz.woitee.endlessRunners.evolution.EvoProgressAccumulator
import cz.woitee.endlessRunners.evolution.evoController.EvoControllerRunner
import cz.woitee.endlessRunners.evolution.evoGame.EvolvedGameDescription
import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.levelGenerators.block.HeightBlockLevelGenerator
import cz.woitee.endlessRunners.gameLaunchers.bitTriGameDefaultBlocks
import io.jenetics.Chromosome
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.util.ISeq

fun doubleGene(value: Double): DoubleGene = DoubleGene.of(value, 0.0, 1.0)
fun speedGene(speed: Double) = doubleGene((speed - 1) / 30.0)
fun scoreGene(score: Int) = doubleGene(((score + 100) / 10).toDouble() / 20)
fun selectorGene(i: Int, count: Int) = doubleGene((i + 0.5) / count)
fun collisionSelectorGene(i: Int, count: Int) = selectorGene(i, count) * 0.5 + 0.5
fun gravityGene(strength: Double) = doubleGene(strength / 2)
fun jumpGene(strength: Double) = doubleGene((strength - 1) / 50.0)
fun customObjectGene(isSolid: Boolean) = doubleGene(if (isSolid) 0.0 else 1.0)

operator fun DoubleGene.plus(value: Double): DoubleGene =
        DoubleGene.of(this.doubleValue() + value, this.min, this.max)
operator fun DoubleGene.times(multiplier: Double): DoubleGene =
        DoubleGene.of(this.doubleValue() * multiplier, this.min, this.max)
operator fun DoubleGene.div(divisor: Double): DoubleGene =
        DoubleGene.of(this.doubleValue() / divisor, this.min, this.max)

val unusedGene = doubleGene(0.0)
val conditional = doubleGene(0.9)

fun bitTriEvolvedGameDescription(): Genotype<DoubleGene> {
    val sampleGenotype = EvolvedGameDescription.sampleGenotype()

    val sampleDescription = EvolvedGameDescription(sampleGenotype)
    println(sampleDescription)

    val newChromosomes = ArrayList<Chromosome<DoubleGene>>()
    // Chromosome 1 - global variables
    newChromosomes.add(sampleGenotype[0].newInstance(ISeq.of(
            speedGene(12.0)
    )))
    // Chromosome 2 - Custom Objects
    newChromosomes.add(sampleGenotype[1].newInstance(ISeq.of(
            { customObjectGene(true) }, 4
    )))
    // Chromosome 3 - Game Conditions
    newChromosomes.add(sampleGenotype[2].newInstance(ISeq.of(
            // PlayerHasColor(YELLOW)
            doubleGene(1.0),
            selectorGene(4, 5),
            // PlayerTouchingObject(GREEN)
            doubleGene(0.0),
            selectorGene(1, 5)
    )))
    // Chromosome 4 - Game Effects
    newChromosomes.add(sampleGenotype[3].newInstance(ISeq.of(
            // GameOver is always included
            // ScoreChange(100)
            doubleGene(0.5),
            scoreGene(100),
            unusedGene,
            unusedGene
    )))
    // Chromosome 5 - Collision Effects
    newChromosomes.add(sampleGenotype[4].newInstance(ISeq.of(
            // GameOver is always included
            // DestroyOther
            doubleGene(0.2),
            unusedGene,
            doubleGene(0.75),
            unusedGene,
            // ScoreChange(100)
            doubleGene(0.6),
            selectorGene(1, 2),
            doubleGene(0.75),
            unusedGene,
            // Conditional(YELLOW, DestroyOther, GameOver)
            conditional,
            selectorGene(0, 2),
            // -- selecting from 3 since self will be filtered out
            selectorGene(1, 3) * 0.5,
            selectorGene(0, 3) * 0.5
    )))
    // Chromosome 6 - Permanent Effects
    newChromosomes.add(sampleGenotype[5].newInstance(ISeq.of(
            // Gravity
            gravityGene(100 * 0.7 / BlockHeight)
    )))
    // Chromosome 7 - Custom Actions
    newChromosomes.add(sampleGenotype[6].newInstance(ISeq.of(
            // Jump
            selectorGene(0, 5) * 0.8,
            jumpGene(17.0),
            unusedGene,
            unusedGene,
            // ChangeShape
            selectorGene(1, 5) * 0.8,
            selectorGene(1, 4) * 0.5,
            unusedGene,
            unusedGene,
            // Trampoline
            conditional,
            selectorGene(1, 2),
            selectorGene(3, 4) * 0.5,
            doubleGene(0.75),
            // ChangeColor
            selectorGene(2, 5) * 0.8,
            selectorGene(1, 3),
            unusedGene,
            unusedGene,
            // (unused) Jump for trampoline
            selectorGene(0, 5) * 0.8,
            jumpGene(25.0),
            doubleGene(0.75),
            unusedGene
    )))

    // Chromosome 8 - Collision Mapping
    // Always in order UP DOWN RIGHT
    newChromosomes.add(sampleGenotype[7].newInstance(ISeq.of(
            // GREEN - default of move to contact
            doubleGene(0.25),
            doubleGene(0.25),
            doubleGene(0.25),
            // RED - GameOver
            collisionSelectorGene(0, 4),
            collisionSelectorGene(0, 4),
            collisionSelectorGene(0, 4),
            // YELLOW - ScoreChange
            collisionSelectorGene(2, 4),
            collisionSelectorGene(2, 4),
            collisionSelectorGene(2, 4),
            // ORANGE - Conditional
            collisionSelectorGene(3, 4),
            collisionSelectorGene(3, 4),
            collisionSelectorGene(3, 4)
    )))

    var i = 0
    return Genotype.of({
        val chromosome = if (i < newChromosomes.size) newChromosomes[i] else sampleGenotype[i]
        ++i
        chromosome
    }, 8)
}

fun main() {
    val genotype = bitTriEvolvedGameDescription()
    val gameDescription = EvolvedGameDescription(genotype)

    val controllerRunner = EvoControllerRunner(
            gameDescription,
            { HeightBlockLevelGenerator(gameDescription, bitTriGameDefaultBlocks(gameDescription)) },
            numGenerations = 500L,
            evoProgressAccumulator = EvoProgressAccumulator()
    )
    val controller = controllerRunner.evolveController()

    controllerRunner.runGame(controller, seed = 42L)
}
