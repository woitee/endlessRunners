package cz.woitee.endlessRunners.utils

/**
 * A simple util class to provide an object that can be passed in a computation pipeline and be periodically checked,
 * if the outer scope doesn't want the computation terminated.
 */
class ComputationStopper {
    var shouldStop = false
        protected set

    fun stop() {
        shouldStop = true
    }
}
