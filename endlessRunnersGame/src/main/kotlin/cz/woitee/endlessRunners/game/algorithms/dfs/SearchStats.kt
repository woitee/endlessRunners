package cz.woitee.endlessRunners.game.algorithms.dfs

/**
 * Data class containing statistics from a single search run.
 *
 * @param searchedStates Total number of states searched.
 * @param backtrackedStates Total number of states backtracked during search.
 * @param reachedDepth Maximum reached depth in the search.
 * @param maxPlayerX Maximum X-coordinate of player reached in the search.
 * @param success Whether the search was successful.
 * @param cachedStates How many states were added into cache.
 * @param timeTaken Time taken by this search.
 */

data class SearchStats(
    var searchedStates: Int = 0,
    var backtrackedStates: Int = 0,
    var reachedDepth: Int = 0,
    var maxPlayerX: Double = Double.MIN_VALUE,
    var success: Boolean = false,
    var cachedStates: Int = 0,
    var timeTaken: Double = 0.0
)
