package game.algorithms

/**
 * Created by woitee on 30/04/2017.
 */

data class SearchStats(
    var searchedStates:Int = 0,
    var backtrackedStates:Int = 0,
    var reachedDepth:Int = 0,
    var success:Boolean = false,
    var timeTaken:Double = 0.0) {
}
