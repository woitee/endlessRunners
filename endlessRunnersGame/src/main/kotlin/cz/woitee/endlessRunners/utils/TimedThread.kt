package cz.woitee.endlessRunners.utils

import java.util.concurrent.atomic.AtomicLong

/**
 * A looping thread to perform tasks as close as possible as to a given FPS value.
 *
 * Meant for use in visualization and game updating.
 * Passes the actual length of update since last to the executing task.
 *
 * @param task The task to perform in each update.
 * @param targetFrameRate Number of updates desired to perform each second.
 * @param useRealTime Whether to actually wait, or just keep making updates as fast as possible.
 */

class TimedThread(val task: (Double) -> Unit, var targetFrameRate: Double, val useRealTime: Boolean = false) : Runnable {
    var running = false
    val thread = Thread(this)
    val cycleTimeMillis = (1000 / targetFrameRate).toLong()
    val cycleTimeSeconds = 1 / targetFrameRate

    var lastUpdateAt = AtomicLong(-1L)

    fun start() {
        running = true
        thread.start()
    }

    override fun run() {
        running = true

        if (!useRealTime) {
            while (running) {
                task(cycleTimeSeconds)
            }
        } else {
            val timeTakenStopwatch = StopWatch()
            val fullCycleStopwatch = StopWatch()

            while (running) {
                fullCycleStopwatch.start()
                timeTakenStopwatch.start()
                task(cycleTimeSeconds)
                lastUpdateAt.set(System.nanoTime())
                val timeTaken = timeTakenStopwatch.stop()
                if (cycleTimeMillis > timeTaken) {
                    Thread.sleep(cycleTimeMillis - timeTaken)
                }
            }
        }
    }

    fun stop(awaitJoin: Boolean = false) {
        running = false
        if (awaitJoin)
            thread.join()
    }

    fun join() {
        thread.join()
    }

    fun join(millis: Long) {
        thread.join(millis)
    }
}
