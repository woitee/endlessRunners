package cz.woitee.game.algorithms.dfs

/**
 * Created by woitee on 30/04/2017.
 */

data class SearchStats(
    var searchedStates:Int = 0,
    var backtrackedStates:Int = 0,
    var reachedDepth:Int = 0,
    var success:Boolean = false,
    var cachedStates:Int = 0,
    var timeTaken:Double = 0.0) {
}
