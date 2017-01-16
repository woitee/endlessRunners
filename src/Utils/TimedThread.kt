package Utils

import javafx.scene.paint.Stop

/**
 * Created by woitee on 14/01/2017.
 */

class TimedThread(val task: (Long)->Unit, var targetFrameRate: Double, val useRealTime:Boolean = false): Runnable {
    var running = false
    val thread = Thread(this)

    fun start() {
        running = true
        thread.start()
    }

    override fun run() {
        val stopWatch = StopWatch()
        val cycleTimeMillis = (1000 / targetFrameRate).toLong()
        val timeTakenStopwatch:StopWatch = StopWatch()
        running = true

        while (running) {
            stopWatch.start()
            if (!useRealTime) {
                task(cycleTimeMillis)
            } else {
                // using real-time means measuring the time and passing it to called thread
                var timeTaken = 0L
                if (timeTakenStopwatch.isStarted) {
                    timeTaken = timeTakenStopwatch.stop()
                } else {
                    timeTaken = cycleTimeMillis
                }
                timeTakenStopwatch.start()
                task(timeTaken)
            }
            val timeTaken = stopWatch.stop()
            if (cycleTimeMillis > timeTaken)
                Thread.sleep(cycleTimeMillis - timeTaken)
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