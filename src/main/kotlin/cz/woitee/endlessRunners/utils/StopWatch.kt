package cz.woitee.endlessRunners.utils

/**
 * Created by woitee on 09/01/2017.
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
        return nanos / 1000000
    }
}
