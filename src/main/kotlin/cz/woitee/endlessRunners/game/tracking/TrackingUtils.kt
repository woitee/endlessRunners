package cz.woitee.endlessRunners.game.tracking

import cz.woitee.endlessRunners.game.actions.ApplyGameEffectAction
import cz.woitee.endlessRunners.game.actions.NoAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.actions.abstract.HoldButtonAction
import cz.woitee.endlessRunners.game.actions.composite.ConditionalAction
import cz.woitee.endlessRunners.game.actions.composite.ConditionalHoldAction
import cz.woitee.endlessRunners.game.collisions.collisionEffects.IUndoableCollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.NoCollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.composite.ConditionalCollisionEffect
import cz.woitee.endlessRunners.game.conditions.GameCondition
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.effects.NoEffect
import cz.woitee.endlessRunners.game.effects.NoTimedEffect
import cz.woitee.endlessRunners.game.effects.UndoableGameEffect
import cz.woitee.endlessRunners.game.effects.composite.ConditionalEffect

/**
 * Utils capable of inserting tracking components into any game.
 */
object TrackingUtils {
    /**
     * Changes every action, effect and condition in the game to a Tracking version of the object.
     * Returns a convenient GameDescriptionTracking object with references to a list of all such actions, objects and
     * effects.
     */
    fun addTracking(gameDescription: GameDescription): GameDescriptionTracking {
        val tracking = GameDescriptionTracking()

        for ((i, action) in gameDescription.allActions.withIndex()) {
            gameDescription.allActions[i] = addActionToTracking(action, tracking)
        }
        for ((i, effect) in gameDescription.permanentEffects.withIndex()) {
            gameDescription.permanentEffects[i] = addEffectToTracking(effect as UndoableGameEffect, tracking)
        }
        for ((i, collisionEffect) in gameDescription.collisionEffects.entries) {
            gameDescription.collisionEffects[i] = addCollisionEffectToTracking(collisionEffect as IUndoableCollisionEffect, tracking)
        }

        return tracking
    }

    private fun addActionToTracking(action: GameAction, tracking: GameDescriptionTracking, isInnerAction: Boolean = false): GameAction {
        if (action is ConditionalAction) {
            action.condition = addConditionToTracking(action.condition, tracking)
            // we could add the separate actions to tracking, but we get that information from toplevel action and conditions
            // inner levels only add other conditions to tracking, but not themselves
            addActionToTracking(action.trueAction, tracking, true)
            addActionToTracking(action.falseAction, tracking, true)
        }
        if (action is ConditionalHoldAction) {
            action.applicableCondition = addConditionToTracking(action.applicableCondition, tracking)
            action.keptApplyingCondition = addConditionToTracking(action.keptApplyingCondition, tracking)
            action.stopApplyingCondition = addConditionToTracking(action.stopApplyingCondition, tracking)
            addActionToTracking(action.holdButtonAction, tracking, true)
        }
        if (action is ApplyGameEffectAction) {
            action.gameEffect = addEffectToTracking(action.gameEffect, tracking)
        }

        return if (action is HoldButtonAction) {
            val trackingAction = TrackingHoldAction(action)
            if (action != NoAction && !isInnerAction) { tracking.holdActions.add(trackingAction) }
            trackingAction
        } else {
            val trackingAction = TrackingAction(action)
            if (action != NoAction && !isInnerAction) { tracking.actions.add(trackingAction) }
            trackingAction
        }
    }
    private fun addEffectToTracking(effect: UndoableGameEffect, tracking: GameDescriptionTracking, isInnerEffect: Boolean = false): TrackingEffect {
        if (effect is ConditionalEffect) {
            effect.condition = addConditionToTracking(effect.condition, tracking)
            addEffectToTracking(effect.trueEffect as UndoableGameEffect, tracking, true)
            addEffectToTracking(effect.falseEffect as UndoableGameEffect, tracking, true)
        }
        val trackingEffect = TrackingEffect(effect)
        if (effect != NoEffect && effect != NoTimedEffect && !isInnerEffect) tracking.effects.add(trackingEffect)
        return trackingEffect
    }
    private fun addCollisionEffectToTracking(effect: IUndoableCollisionEffect, tracking: GameDescriptionTracking, isInnerEffect: Boolean = false): TrackingCollisionEffect {
        if (effect is ConditionalCollisionEffect) {
            effect.condition = addConditionToTracking(effect.condition, tracking)
            addCollisionEffectToTracking(effect.trueEffect as IUndoableCollisionEffect, tracking, true)
            addCollisionEffectToTracking(effect.falseEffect as IUndoableCollisionEffect, tracking, true)
        }
        val trackingEffect = TrackingCollisionEffect(effect)
        if (effect != NoCollisionEffect && !isInnerEffect) tracking.collisionEffects.add(trackingEffect)
        return trackingEffect
    }
    private fun addConditionToTracking(condition: GameCondition, tracking: GameDescriptionTracking): TrackingCondition {
        val trackingCondition = TrackingCondition(condition)
        tracking.conditions.add(trackingCondition)
        return trackingCondition
    }
}
