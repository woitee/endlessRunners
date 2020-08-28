package cz.woitee.endlessRunners.game.algorithms.dfs

import java.util.*
import kotlin.repeat

/**
 * A methods that averages statistics of searches to batches and prints them once in a while.
 *
 * @param sumEvery Number of iterations after we perform the averaging and print.
 * @param callback What should happen after this number of iterations - default is printing to standard output.
 */

class SearchStatsSummer(val sumEvery: Int, val callback: (SearchStatsAverage) -> Unit = { average -> println(average) }) {
    data class SearchStatsAverage(
        var count: Int = 0,
        var searchedStates: Double = 0.0,
        var backtrackedStates: Double = 0.0,
        var reachedDepth: Double = 0.0,
        var success: Double = 0.0,
        var cachedStates: Double = 0.0,
        var timeTaken: Double = 0.0
    ) {

        override fun toString(): String {
            fun roundDouble(x: Double, precision: Int = 2): Double {
                var dec = 1
                repeat(precision, { dec *= 10 })
                return Math.round(x * dec).toDouble() / dec
            }
            return "SearchStatsAverage(" +
                "count=$count, " +
                "searched=${roundDouble(searchedStates)}, " +
                "backtracked=${roundDouble(backtrackedStates)}, " +
                "reachedDepth=${roundDouble(reachedDepth)}, " +
                "success=${roundDouble(success)}, " +
                "cachedStates=${roundDouble(cachedStates)}, " +
                "timeTaken=${roundDouble(timeTaken * 1000)}ms" +
                ")"
        }
    }

    val myStatsList = ArrayList<SearchStats>()

    fun noteStats(stats: SearchStats) {
        myStatsList.add(stats)
        if (myStatsList.count() >= sumEvery) {
            callback(average(myStatsList))
            myStatsList.clear()
        }
    }

    fun average(statsList: List<SearchStats>): SearchStatsAverage {
        val result = SearchStatsAverage()
        if (statsList.isEmpty())
            return result

        for (stats in statsList) {
            result.searchedStates += stats.searchedStates
            result.backtrackedStates += stats.backtrackedStates
            result.reachedDepth += stats.reachedDepth
            result.success += if (stats.success) 1 else 0
            result.cachedStates += stats.cachedStates
            result.timeTaken += stats.timeTaken
        }
        result.searchedStates /= statsList.count()
        result.backtrackedStates /= statsList.count()
        result.reachedDepth /= statsList.count()
        result.cachedStates /= statsList.count()
        result.success /= statsList.count()
        result.timeTaken /= statsList.count()
        result.count = statsList.count()

        return result
    }
}
