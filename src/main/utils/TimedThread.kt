package utils

/**
 * Created by woitee on 14/01/2017.
 */

class TimedThread(val task: (Double)->Unit, var targetFrameRate: Double, val useRealTime:Boolean = false): Runnable {
    var running = false
    val thread = Thread(this)
    val cycleTimeMillis = (1000 / targetFrameRate).toLong()
    val cycleTimeSeconds = 1 / targetFrameRate

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
}