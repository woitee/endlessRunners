package cz.woitee.endlessRunners.experiments

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * A combination of a reaction and a precision test, occuring in order.
 */
class ReactionAndPrecisionTest {
    /**
     * Run both a reaction and a precision test.
     */
    fun run() {
        val reactionTest = ReactionTest()
        reactionTest.run()
        val hitPrecisionTest = HitPrecisionTest()
        hitPrecisionTest.run()

        val filename = "ReactionTest_${SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date())}.log"
        println("Saving reactionTest log to file $filename")

        val file = File(filename)
        val writer = file.bufferedWriter()

        writer.write(SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()).toString())
        writer.newLine()
        for (time in reactionTest.reactionTimes) {
            writer.write("$time")
            writer.newLine()
        }
        writer.newLine()
        for (time in hitPrecisionTest.precisionTimes) {
            writer.write("$time")
            writer.newLine()
        }

        writer.flush()
        writer.close()
    }
}
