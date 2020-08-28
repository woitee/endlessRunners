package cz.woitee.endlessRunners.game.tracking

/**
 * A tracker that holds statistics based on the actions, effects, etc. that occured in a game.
 */
class GameDescriptionTracking {
    val actions = ArrayList<TrackingAction>()
    val holdActions = ArrayList<TrackingHoldAction>()
    val effects = ArrayList<TrackingEffect>()
    val collisionEffects = ArrayList<TrackingCollisionEffect>()
    val conditions = ArrayList<TrackingCondition>()

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine("GameDescription Tracking")
        sb.appendLine("========================")

        for (action in actions) {
            sb.appendLine("Action $action was used ${action.timesUsed} (${action.timesUndone} undone) times")
        }
        for (action in holdActions) {
            sb.appendLine(
                "Action $action was started ${action.timesStarted} (${action.timesUndoneStart} undone) times, " +
                    "stopped ${action.timesStopped} (${action.timesUndoneStop} undone) times and " +
                    "kept applied for ${action.timesKeptHeld} (${action.timesUndoneKeepHolding} undone) frames in total."
            )
        }
        for (effect in effects) {
            sb.appendLine("Effect $effect occured in ${effect.timesApplied} (${effect.timesUndone} undone) frames")
        }
        for (collEffect in collisionEffects) {
            sb.appendLine("Collision Effect $collEffect occured in ${collEffect.timesApplied} (${collEffect.timesUndone} undone) frames")
        }
        for (condition in conditions) {
            sb.appendLine("Condition $condition was evaluated ${condition.timesEvaluated} times (${condition.trueEvaluations} true, ${condition.falseEvaluations} false)")
        }
        return sb.toString()
    }
}
