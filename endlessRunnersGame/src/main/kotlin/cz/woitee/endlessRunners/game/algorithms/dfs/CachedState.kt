package cz.woitee.endlessRunners.game.algorithms.dfs

import cz.woitee.endlessRunners.game.GameState
import java.io.Serializable

/**
 * A class containing data of a state that serve as a hash key.
 */
data class CachedState(val playerX: Double, val playerY: Double, val playerYSpeed: Double, var heldActionFlags: Int) : Serializable {
    constructor (gameState: GameState) : this(
        gameState.player.x,
        gameState.player.y,
        gameState.player.yspeed,
        gameState.heldButtonsAsFlags()
    )
}
