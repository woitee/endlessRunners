package cz.woitee.endlessRunners.game.tracking

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.geom.Vector2Int

/**
 * A tracker that holds statistics by sampling the GameState in each frame.
 */
class GameStateTracking {
    var timeAirborne = 0
        protected set
    var timeOutOfScreen = 0
        protected set
    var timeInOtherDimensions: HashMap<Vector2Int, Int> = HashMap()
    var numInits = 0

    protected val jumpAction = JumpAction(1.0)

    /**
     * Record another gameState and add it to statistics.
     */
    fun addSnapshot(gameState: GameState) {
        val player = gameState.player

        if (!jumpAction.isApplicableOn(gameState))
            ++timeAirborne
        if (!gameState.grid.contains(gameState.gridLocation(player.location)))
            ++timeOutOfScreen
        if (player.widthBlocks != player.defaultWidthBlocks || player.heightBlocks != player.defaultHeightBlocks) {
            val key = Vector2Int(player.widthBlocks, player.heightBlocks)
            if (!timeInOtherDimensions.containsKey(key)) timeInOtherDimensions[key] = 0
            timeInOtherDimensions[key] = timeInOtherDimensions[key]!! + 1
        }
        if (gameState.gameTime < 0.0001)
            ++numInits
    }

    /**
     * Reset this tracking.
     */
    fun clear() {
        timeAirborne = 0
        timeOutOfScreen = 0
        timeInOtherDimensions.clear()
        numInits = 0
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendln("GameState Tracking")
        sb.appendln("==================")
        sb.appendln("Time airborne: $timeAirborne frames")
        sb.appendln("Time out of screen: $timeOutOfScreen frames")
        sb.appendln("Other Dims:")
        for ((shape, time) in timeInOtherDimensions) {
            sb.append("  (width=${shape.x}, height=${shape.y}): $time frames")
        }
        return sb.toString()
    }
}
