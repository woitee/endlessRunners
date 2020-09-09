package cz.woitee.endlessRunners.evolution.evoGame

import cz.woitee.endlessRunners.evolution.utils.MathUtils
import cz.woitee.endlessRunners.game.BlockWidth
import cz.woitee.endlessRunners.game.GameFPS
import cz.woitee.endlessRunners.game.actions.*
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.actions.composite.ConditionalAction
import cz.woitee.endlessRunners.game.collisions.BaseCollisionHandler
import cz.woitee.endlessRunners.game.collisions.collisionEffects.*
import cz.woitee.endlessRunners.game.collisions.collisionEffects.composite.ConditionalCollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.composite.MultipleCollisionEffect
import cz.woitee.endlessRunners.game.conditions.GameCondition
import cz.woitee.endlessRunners.game.conditions.PlayerHasColor
import cz.woitee.endlessRunners.game.conditions.PlayerTouchingObject
import cz.woitee.endlessRunners.game.conditions.TrueCondition
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.effects.*
import cz.woitee.endlessRunners.game.effects.composite.ConditionalEffect
import cz.woitee.endlessRunners.game.objects.*
import cz.woitee.endlessRunners.geom.Direction4
import cz.woitee.endlessRunners.geom.asFlagsContains
import io.jenetics.Chromosome
import io.jenetics.DoubleChromosome
import io.jenetics.DoubleGene
import io.jenetics.Genotype
import io.jenetics.util.IntRange
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * This class encompasses a genotype and interprets it as a GameDescription.
 * It also provides the sample GenoType to start evolution from and other utility methods.
 */
class EvolvedGameDescription(val genotype: Genotype<DoubleGene>, val limitForDFS: Boolean = false) : GameDescription() {
    /**
     * A helper class to specify a chromosome with sets of pack.
     *
     * @param min Minimum number of gene packs in this chromosome.
     * @param max Maximum number of gene packs in this chromosom.
     * @param numAttributes Number of genes within a pack.
     */
    data class ChromosomeSpec(val min: Int, val max: Int, val numAttributes: Int = 1) {
        /**
         * Creates a chromosome that fits the rules of this specification.
         */
        fun toChromosome(): DoubleChromosome {
            if (max == min) {
                return DoubleChromosome.of(0.0, 1.0, min * numAttributes)
            }
            return DoubleChromosome.of(0.0, 1.0, IntRange.of(min * numAttributes, max * numAttributes + 1))
        }
    }

    /**
     * A class providing logical "gene-pack" accessibility to a chromosome, together with iteration.
     */
    class GenePack(var chromosome: Chromosome<DoubleGene>, var size: Int = 0) : Iterator<GenePack> {
        var currentOffset = -size
            protected set
        var currentIndex = -1
            protected set

        override fun hasNext(): Boolean {
            return currentOffset + 2 * size <= chromosome.length()
        }
        override fun next(): GenePack {
            currentOffset += size
            currentIndex += 1
            return this
        }
        fun gene(i: Int): Double {
            assert(i < size)
            return chromosome.getGene(currentOffset + i).allele
        }
    }

    companion object {
        // to see description of what the genes mean, see the init function
        val customObjectsSpec = ChromosomeSpec(1, 4)
        val gameConditionsSpec = ChromosomeSpec(1, 4, 2)
        val customEffectsSpec = ChromosomeSpec(1, 5, 4) // two additional only for conditional effects
        val customActionsSpec = ChromosomeSpec(2, 5, 4) // two additional only for conditional actions
        val collisionEffectsSpec = ChromosomeSpec(1, 5, 4) // three additional only for conditional effects
        val permanentEffectsSpec = ChromosomeSpec(1, 3)
        // player collision with any custom object
        val collisionMappingSpec = ChromosomeSpec(min(1, customObjectsSpec.min), 1 + customObjectsSpec.max, 3) // we only separate Right, Up and Down collisions
        val globalVariablesSpec = ChromosomeSpec(1, 1)

        /**
         * A sample genotype for GameDescriptions, usable for starting the evolution.
         */
        fun sampleGenotype(): Genotype<DoubleGene> {
            // Each chromosome depicts one aspect of the game (listed above)
            return Genotype.of(
                globalVariablesSpec.toChromosome(),
                customObjectsSpec.toChromosome(),
                gameConditionsSpec.toChromosome(),
                customEffectsSpec.toChromosome(),
                collisionEffectsSpec.toChromosome(),
                permanentEffectsSpec.toChromosome(),
                customActionsSpec.toChromosome(),
                collisionMappingSpec.toChromosome()
            )
        }

        // Only speeds that will traverse exactly 2 blocks in a number of updates are allowed
        val possibleSpeeds: List<Double> = (1 .. 100).map { GameFPS / (2.0 * it) }
    }

    override val customObjects = ArrayList<GameObject>()
    override val allActions = ArrayList<GameAction>()
    val allEffects = arrayListOf<GameEffect>(
        GameOver()
    )
    val allCollisionEffects = arrayListOf<ICollisionEffect>(
        ApplyGameEffect(GameOver())
    )
    override val permanentEffects = ArrayList<GameEffect>()
    val allConditions = ArrayList<GameCondition>()

    init {
        // Parsing the genes to the various parameters of a game description

        // Chromosome 1 - Game Global Variables (e.g. starting speed)
        for (genePack in GenePack(genotype[0], globalVariablesSpec.numAttributes)) {
            val gene = genePack.gene(0)
            when (genePack.currentIndex) {
                0 -> playerStartingSpeed = roundPlayerSpeed(1.0 + 30.0 * gene)
            }
        }
        // Chromosome 2 - Custom Objects (gene determines solidness of object)
        for (genePack in GenePack(genotype[1], customObjectsSpec.numAttributes)) {
            customObjects.add(
                CustomBlock(
                    genePack.currentIndex,
                    genePack.gene(0) < 0.5
                )
            )
        }
        // Chromosome 3 - Game Conditions
        for (genePack in GenePack(genotype[2], gameConditionsSpec.numAttributes)) {
            allConditions.add(getConditionFromGenes(genePack.gene(0), genePack.gene(1)))
        }
        // Chromosome 4 - Game Effects (genes say the type and the details of effects)
        // Some of these are timed - apply only temporarily
        for (genePack in GenePack(genotype[3], customEffectsSpec.numAttributes)) {
            val gene1 = genePack.gene(0)
            val gene2 = genePack.gene(1)
            allEffects.add(
                if (isTimedEffect(gene1) || isConditional(gene1)) NoEffect
                else getNonTimedEffectFromGenes(gene1, gene2)
            )
        }
        for (genePack in GenePack(genotype[3], customEffectsSpec.numAttributes)) {
            val gene1 = genePack.gene(0)
            val gene2 = genePack.gene(1)
            if (!isTimedEffect(gene1)) continue
            // +1 since GameOver isn't counted
            allEffects[genePack.currentIndex + 1] = getTimedEffectFromGenes(gene1, gene2)
        }
        for (genePack in GenePack(genotype[3], customEffectsSpec.numAttributes)) {
            if (!isConditional(genePack.gene(0))) continue
            // +1 since GameOver isn't counted
            allEffects[genePack.currentIndex + 1] = getConditonalEffectFromGenes(
                genePack.gene(0),
                genePack.gene(1),
                genePack.gene(2),
                genePack.gene(3)
            )
        }
        // Chromosome 5 - Collision Effects (all effects that may happen when player collides with something)
        for (genePack in GenePack(genotype[4], collisionEffectsSpec.numAttributes)) {
            allCollisionEffects.add(
                if (isConditional(genePack.gene(0))) NoCollisionEffect else getCollisionEffectFromGenes(genePack.gene(0), genePack.gene(1), genePack.gene(2))
            )
        }
        for (genePack in GenePack(genotype[4], collisionEffectsSpec.numAttributes)) {
            if (!isConditional(genePack.gene(0))) continue
            // +1 since GameOver isn't counted
            allCollisionEffects[genePack.currentIndex + 1] = getConditionalCollisionEffectFromGenes(genePack.gene(0), genePack.gene(1), genePack.gene(2), genePack.gene(3))
        }
        // Chromosome 6 - Permanent Effects (effects that occur on every update (main notable - Gravity)
        for (genePack in GenePack(genotype[5], permanentEffectsSpec.numAttributes)) {
            val gene = genePack.gene(0)
            val permEffect = when (genePack.currentIndex) {
                0 -> Gravity(GameEffect.Target.PLAYER, gene * 8)
                else -> {
                    val candidate = selectByGene(allEffects, gene)
                    if (candidate !is GameOver) candidate else null
                }
            }
            if (permEffect != null) permanentEffects.add(permEffect)
        }
        // Chromosome 7 - Game Actions (genes say the type and a single attribute of action)
        for (genePack in GenePack(genotype[6], customActionsSpec.numAttributes)) {
            allActions.add(
                if (isConditional(genePack.gene(0))) NoAction else getActionFromGenes(genePack.gene(0), genePack.gene(1), genePack.gene(2))
            )
        }
        for (genePack in GenePack(genotype[6], customActionsSpec.numAttributes)) {
            if (!isConditional(genePack.gene(0))) continue
            allActions[genePack.currentIndex] = getConditionalActionFromGenes(genePack.gene(0), genePack.gene(1), genePack.gene(2), genePack.gene(3))
        }
        // Some non-conditional actions could have been "disabled" by gene3 - they are only used for conditional actions
        for (genePack in GenePack(genotype[6], customActionsSpec.numAttributes)) {
            if (isConditional(genePack.gene(0))) continue
            if (genePack.gene(2) >= 0.5) allActions[genePack.currentIndex] = NoAction
        }
        // Chromosome 8 - Collision Mapping (genes say the success of collision Player x Object)
        for (genePack in GenePack(genotype[7], collisionMappingSpec.numAttributes)) {
            addColllisionMapFromGenes(genePack)
        }
    }

    /**
     * Whether the gene corresponds to the range where it should be a TimedEffect instead of a normal effect.
     */
    protected fun isTimedEffect(gene1: Double): Boolean {
        return gene1 >= 0.6 && gene1 < 0.8
    }
    /**
     * Whether the gene is in the range that corresponds to it being a conditional effect.
     */
    protected fun isConditional(gene1: Double): Boolean {
        return gene1 >= 0.8
    }

    /**
     * A method using a gene value to select from a list.
     */
    fun <T> selectByGene(list: List<T>, gene: Double): T? {
        if (list.isEmpty()) return null
        val ix = (gene * list.count()).toInt().coerceIn(0, list.lastIndex)
        return list[ix]
    }

    /**
     * Extracting the GameCondition represented by two genes.
     */
    fun getConditionFromGenes(gene1: Double, gene2: Double): GameCondition {
        return when {
            gene1 < 0.5 -> {
                val possibleObjects = arrayListOf(GameObjectClass.SOLIDBLOCK)
                customObjects.indices.mapTo(possibleObjects) { GameObjectClass.fromInt(GameObjectClass.CUSTOM0.ord + it) }

                val objectClass = selectByGene(possibleObjects, gene2)
                if (objectClass != null) PlayerTouchingObject(Direction4.DOWN, objectClass) else TrueCondition()
            }
            else -> {
                val color = GameObjectColor.fromInt((gene2 * 5).toInt())
                PlayerHasColor(color)
            }
        }
    }

    /**
     * Extract non-timed effect from two Double genes - first describes the type and second the properties (with variable
     * meaning depending on the type).
     */
    fun getNonTimedEffectFromGenes(gene1: Double, gene2: Double): GameEffect {
        return when {
            // Gravity (with strength -2 to +8)
            gene1 < 0.2 -> Gravity(GameEffect.Target.PLAYER, -2 + gene2 * 10)
            // SpeedChange (either absolute from 12 to 36, or relative of -5 to +5)
            gene1 < 0.4 -> {
                if (gene2 < 0.5) SpeedChange(GameEffect.Target.PLAYER, 12.0 + gene2 * 48)
                else SpeedChange(GameEffect.Target.PLAYER, -5 + (gene2 - 0.5) * 20, GameEffect.Relativity.RELATIVE)
            }
            // ScoreChange (in range -100, -40, ... 40, 100)
            gene1 < 0.6 -> ScoreChange((gene2 * 20).toInt() * 10 - 100)

            isTimedEffect(gene1) || isConditional(gene1) -> throw Exception("This section is reserved for timed and conditional effects.")
            else -> throw Exception("This shouldn't happen")
        }
    }
    /**
     * Extract a timed or conditional effect from the genes (gene of a timed effect refers to a non-timed effect from the others).
     * The function expects that the allEffects variable is already filled with non-timed effects, having NoEffect values
     * for where the TimedEffects should be.
     *
     * The first gene is unused (it should be >=0.75 and the second gene describes which effect to use).
     */
    fun getTimedEffectFromGenes(gene1: Double, gene2: Double): TimedEffect {
        assert(isTimedEffect(gene1))
        val otherEffects = allEffects.filter { it != NoEffect && it != NoTimedEffect }
        if (otherEffects.isEmpty()) return NoTimedEffect

        val targetEffect = selectByGene(otherEffects, gene2)
        val timeout = 1.0

        return when {
            // Gravity is just turned on and off
            targetEffect is Gravity -> TimedEffect(timeout, NoEffect, NoEffect, targetEffect)
            // SpeedChange occurs either on and off (if it is absolute) or there and back (if it is relative)
            targetEffect is SpeedChange -> {
                if (targetEffect.relativity == GameEffect.Relativity.RELATIVE)
                    TimedEffect(timeout, targetEffect, targetEffect.oppositeEffect as UndoableGameEffect)
                else
                    TimedEffect(timeout, targetEffect, SpeedChange(targetEffect.target, playerStartingSpeed))
            }
            // ScoreChange can get reverted - this is here because of keeping the general principle, even though we
            // dont see meaning
            targetEffect is ScoreChange -> TimedEffect(timeout, targetEffect, targetEffect.oppositeEffect as UndoableGameEffect)
            else -> NoTimedEffect
        }
    }

    /**
     * Extracting a conditional effect from 4 genes that represent it.
     */
    fun getConditonalEffectFromGenes(gene1: Double, gene2: Double, gene3: Double, gene4: Double): GameEffect {
        return getConditionalFromGenes(gene2, gene3, gene4, allEffects, NoEffect, { cond, a, b -> ConditionalEffect(cond, a, b) })
    }

    /**
     * This function filters effects we do not necessarily want to use for actions,
     * mainly because using some for DFS is dangerous, since they can lead to an extreme amount of states.
     *
     * Namely relative SpeedChange could get to basically any double value of x coordinate, which is unmanageable.
     * Similiarly using Gravity as an action could get the player to a lot of possible y coordinates.
     */
    protected fun isDangerousForAction(effect: GameEffect): Boolean {
        if (!limitForDFS) return false

        return when (effect) {
            is SpeedChange -> effect.relativity == GameEffect.Relativity.RELATIVE
            is Gravity -> true
            is TimedEffect -> isDangerousForAction(effect.startEffect) || isDangerousForAction(effect.stopEffect) || isDangerousForAction(effect.runningEffect)
            is ConditionalEffect -> isDangerousForAction(effect.trueEffect) || isDangerousForAction(effect.falseEffect)
            else -> false
        }
    }

    /**
     * Extract action from two Double genes - first describes the type and second the properties (with variable
     * meaning depending on the type).
     */
    fun getActionFromGenes(gene1: Double, gene2: Double, gene3: Double): GameAction {
        val part = 0.8 / 5

        return when {
            // JumpAction with jump power between 1 and 51
            gene1 < part -> JumpAction(1.0 + gene2 * 50.0)
            // ChangeShapeHoldAction creating a (horizontal/vertical) stick between 1 and 4 tiles long
            gene1 < 2 * part -> ChangeShapeHoldAction(
                if (gene2 < 0.5) (gene2 * 6).roundToInt() + 1 else 1,
                if (gene2 < 0.5) 1 else ((gene2 - 0.5) * 6).roundToInt() + 1
            )
            // ChangeColorHoldAction with a random color
            gene1 < 3 * part -> ChangeColorHoldAction(
                selectByGene(
                    listOf(
                        GameObjectColor.GREEN,
                        GameObjectColor.YELLOW,
                        GameObjectColor.ORANGE
                    ),
                    gene2
                )!!
            )
            // DoubleJumpAction with jump power between 1 and 51
            gene1 < 4 * part -> {
                MultiJumpAction(1.0 + gene2 * 50.0, 1 + (gene3 * 2).toInt())
            }
            // Apply some TimedGameEffect
            gene1 < 5 * part -> {
                ApplyGameEffectAction(
                    selectByGene(allEffects.filter { !isDangerousForAction(it) }, gene2) as UndoableGameEffect
                )
            }
            else -> throw Exception("This is reserved for conditional actions")
        }
    }

    /**
     * Extracting a conditional action from 4 genes that represent it.
     */
    fun getConditionalActionFromGenes(gene1: Double, gene2: Double, gene3: Double, gene4: Double): GameAction {
        return getConditionalFromGenes(gene2, gene3, gene4, allActions, NoAction) { cond, a, b -> ConditionalAction(cond, a, b) }
    }

    /**
     * Transforming a genePack into a CollisionEffect.
     */
    fun getCollisionEffectFromGenes(gene1: Double, gene2: Double, gene3: Double): ICollisionEffect {
        var desiredEffect = when {
            gene1 < 0.4 -> MoveToContact()
            gene1 < 0.8 -> {
                val effect = selectByGene(allEffects, gene2)
                if (effect is UndoableGameEffect) ApplyGameEffect(effect) else MoveToContact()
            }
            else -> throw Exception("This is reserved for conditional collision effects")
        }

        if (gene3 >= 0.5) {
            desiredEffect = if (gene1 < 0.4) DestroyOther() else MultipleCollisionEffect(desiredEffect, DestroyOther())
        }
        return desiredEffect
    }

    /**
     * Extracting a conditional collision effect from 4 genes that represent it.
     */
    fun getConditionalCollisionEffectFromGenes(gene1: Double, gene2: Double, gene3: Double, gene4: Double): ICollisionEffect {
        return getConditionalFromGenes(gene2, gene3, gene4, allCollisionEffects, MoveToContact()) { cond, a, b -> ConditionalCollisionEffect(cond, a, b) }
    }

    /**
     * Whether the MoveToContact collision effect is nested somewhere within a given collision effect.
     */
    protected fun containsMoveToContact(collEffect: ICollisionEffect): Boolean {
        return when (collEffect) {
            is MoveToContact -> true
            is ConditionalCollisionEffect -> containsMoveToContact(collEffect.trueEffect) || containsMoveToContact(collEffect.falseEffect)
            is MultipleCollisionEffect -> collEffect.innerEffects.any { containsMoveToContact(it) }
            else -> false
        }
    }

    /**
     * Transforms a genePack into one entry in the collision map.
     */
    fun addColllisionMapFromGenes(genePack: GenePack) {
        val index = genePack.currentIndex
        val otherClass = GameObjectClass.fromInt(GameObjectClass.SOLIDBLOCK.ord + index)
        for ((i, direction) in arrayOf(Direction4.UP, Direction4.DOWN, Direction4.RIGHT).withIndex()) {
            val gene = genePack.gene(i)
            if (gene < 0.5) continue
            val entry = BaseCollisionHandler.CollisionHandlerEntry(GameObjectClass.PLAYER, otherClass, direction)
            val collisionEffect = selectByGene(allCollisionEffects, (gene - 0.5) * 2) ?: continue
            // Forbid MoveToContact in direction Right - it does unwanted things
            if (entry.directionFlags.asFlagsContains(Direction4.RIGHT) && containsMoveToContact(collisionEffect)) continue
            collisionEffects[entry] = collisionEffect
        }
        // Collision in direction RIGHT with solid block should always be game over
        collisionEffects[BaseCollisionHandler.CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.SOLIDBLOCK, Direction4.RIGHT)] = ApplyGameEffect(GameOver())
    }

    // ================================
    // ||                            ||
    // ||  PROTECTED FUNCTIONS ZONE  ||
    // ||                            ||
    // ================================

    protected fun roundPlayerSpeed(speed: Double) = possibleSpeeds.minByOrNull { abs(it - speed) }!!.toDouble()

    /**
     * This method creates a conditional (effect | action | collisionEffect) from 3 genes, list where to find other objects
     * of its' type, the default value which will be used for filtering in the 'others' array and also returned if the fetch
     * fails, and a combining function that can create a conditional object from a GameCondition and two such objects.
     *
     * @param gene2 Gene used for selecting a condition
     * @param gene3 Gene used for selecting a positive effect
     * @param gene4 Gene used for selecting a negetive effect
     * @param others List of results to choose from
     * @param default The default value to choose
     * @param creator A function that assembles the condition and the two chosen elements into a Conditional
     */
    protected fun <T> getConditionalFromGenes(gene2: Double, gene3: Double, gene4: Double, others: List<T>, default: T, creator: (GameCondition, T, T) -> T): T {
        val othersList = others.filter { it != default }
        if (othersList.isEmpty()) return default

        val condition = selectByGene(allConditions, gene2)
        val positiveEffect = if (gene3 >= 0.5) default else selectByGene(othersList, gene3 * 2)
        val negativeEffect = if (gene4 >= 0.5) default else selectByGene(othersList, gene4 * 2)

        if (condition == null || positiveEffect == null || negativeEffect == null) return default
        if (positiveEffect == negativeEffect) return default
        return creator(condition, positiveEffect, negativeEffect)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine("EvolvedGameDescription (${customObjects.count()} custom objects)")
        sb.appendLine("======")
        sb.appendLine("Starting speed is $playerStartingSpeed")
        sb.appendLine("ACTIONS")
        for (action in allActions) {
            sb.appendLine(action.toString())
        }
        sb.appendLine("EFFECTS")
        for (effect in allEffects) {
            sb.appendLine(effect)
        }
        sb.appendLine("PERMANENT EFFECTS")
        for (effect in permanentEffects) {
            sb.appendLine(effect)
        }
        sb.appendLine("COLLISION MAPPING")
        for ((entry, effect) in collisionEffects.entries) {
            sb.appendLine("$entry -> $effect")
        }
        sb.appendLine()

        return sb.toString()
    }
}
