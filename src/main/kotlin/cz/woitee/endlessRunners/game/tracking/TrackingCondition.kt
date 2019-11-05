package cz.woitee.endlessRunners.game.tracking

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.conditions.GameCondition

/**
 A wrapper for a condition that tracks times evaluated as true and as false.
 */
class TrackingCondition(val innerCondition: GameCondition) : GameCondition() {
    var trueEvaluations = 0
    var falseEvaluations = 0
    val timesEvaluated
        get() = trueEvaluations + falseEvaluations

    override fun isTrue(gameState: GameState): Boolean {
        val result = innerCondition.isTrue(gameState)
        if (result) ++trueEvaluations else ++falseEvaluations
        return result
    }

    override fun toString(): String {
        return innerCondition.toString()
    }
}
