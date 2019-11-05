package cz.woitee.endlessRunners.utils

/**
 * A class to quickly measure time intervals in microseconds, using nanosecond precision methods.
 */
internal class StopWatch {
    var startTime = 0L
    var isStarted = false
        private set

    fun start() {
        isStarted = true
        startTime = System.nanoTime()
    }

    fun stop(): Long {
        isStarted = true
        val nanos = System.nanoTime() - startTime
        return (nanos / 1000000) + (if (nanos.rem(1000000L) >= 500000) 1 else 0)
    }
}
