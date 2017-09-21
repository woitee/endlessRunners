package game.gameActions.abstract

import game.GameState

/**
 * A class facilitating an action that can be held for a duration of time.
 * Except for being an interface, it facilitates some of the mandatory things for a hold action,
 * e.g. logging the time start into GameState's heldActions variable.
 *
 * You can extract non-hold Actions from it by getting asStartAction, and asStopAction.
 *
 * Created by woitee on 23/07/2017.
 */
abstract class HoldAction : GameAction() {
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
        innerStopApplyingOn(gameState, gameState.heldActions[this]!!)
        gameState.heldActions.remove(this)
    }
    fun canBeStoppedApplyingOn(gameState: GameState): Boolean {
        return gameState.heldActions.containsKey(this) && this.innerCanBeStoppedApplyingOn(gameState)
    }
    fun canBeKeptApplyingOn(gameState: GameState): Boolean {
        return gameState.heldActions.containsKey(this) && this.innerCanBeKeptApplyingOn(gameState)
    }

    abstract protected fun innerIsApplicableOn(gameState: GameState): Boolean
    abstract protected fun innerCanBeKeptApplyingOn(gameState: GameState): Boolean
    abstract protected fun innerCanBeStoppedApplyingOn(gameState: GameState): Boolean
    abstract protected fun innerApplyOn(gameState: GameState)
    abstract protected fun innerStopApplyingOn(gameState: GameState, timeStart: Double)
}