package cz.woitee.game.actions.abstract

import cz.woitee.game.GameState

/**
 * A class facilitating an action that can be held for a duration of time.
 * Except for being an interface, it facilitates some of the mandatory things for a hold action,
 * e.g. logging the time start into GameState's heldActions variable.
 *
 * You can extract non-hold Actions from it by getting asStartAction, and asStopAction.
 *
 * Created by woitee on 23/07/2017.
 */
abstract class HoldAction(val minimumHoldTime: Double) : GameAction() {
    class AsStopAction(val holdAction: HoldAction): GameAction() {
        override fun applyOn(gameState: GameState) {
            holdAction.stopApplyingOn(gameState)
        }
        override fun isApplicableOn(gameState: GameState): Boolean {
            return holdAction.canBeStoppedApplyingOn(gameState)
        }
    }
    open val asStartAction: GameAction
        get() = this
    open val asStopAction: GameAction = AsStopAction(this)

    override final fun isApplicableOn(gameState: GameState): Boolean {
        return !gameState.heldActions.containsKey(this) && this.innerIsApplicableOn(gameState)
    }
    override final fun applyOn(gameState: GameState) {
        innerApplyOn(gameState)
        gameState.heldActions[this] = gameState.gameTime
    }
    fun stopApplyingOn(gameState: GameState) {
        val heldActionTime = gameState.heldActions[this]!!
        gameState.heldActions.remove(this)
        innerStopApplyingOn(gameState, heldActionTime)
    }
    fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        if (!gameState.heldActions.containsKey(this))
            return false

        val heldTime = gameState.gameTime - gameState.heldActions[this]!!
        return heldTime >= minimumHoldTime && this.innerCanBeStoppedApplyingOn(gameState)
    }
    fun canBeKeptApplyingOn(gameState: GameState): Boolean {
        return gameState.heldActions.containsKey(this) && this.innerCanBeKeptApplyingOn(gameState)
    }

    abstract internal fun innerIsApplicableOn(gameState: GameState): Boolean
    abstract internal fun innerCanBeKeptApplyingOn(gameState: GameState): Boolean
    abstract internal fun innerCanBeStoppedApplyingOn(gameState: GameState): Boolean
    abstract internal fun innerApplyOn(gameState: GameState)
    abstract internal fun innerStopApplyingOn(gameState: GameState, timeStart: Double)
}