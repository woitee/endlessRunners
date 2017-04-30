package Game.Algorithms

import java.util.*

/**
 * Created by woitee on 30/04/2017.
 */

class SearchStatsSummer(val sumEvery:Int, val callback:(SearchStatsAverage)->Unit = { average -> println(average) }) {
    data class SearchStatsAverage(
        var searchedStates: Double = 0.0,
        var backtrackedStates: Double = 0.0,
        var reachedDepth: Double = 0.0,
        var success: Double = 0.0,
        var timeTaken: Double = 0.0
    )

    val myStatsList = ArrayList<SearchStats>()

    fun noteStats(stats: SearchStats) {
        myStatsList.add(stats)
        if (myStatsList.count() > sumEvery) {
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
            result.timeTaken += stats.timeTaken
        }
        result.searchedStates /= statsList.count()
        result.backtrackedStates /= statsList.count()
        result.reachedDepth /= statsList.count()
        result.success /= statsList.count()
        result.timeTaken /= statsList.count()

        return result
    }
}