package cz.woitee.game.algorithms.dfs

import cz.woitee.game.GameState
import java.io.Serializable

data class CachedState(val playerX: Double, val playerY: Double, val playerYSpeed: Double, var heldActionFlags: Int): Serializable {
    constructor (gameState: GameState): this(
            gameState.player.x,
            gameState.player.y,
            gameState.player.yspeed,
            gameState.heldButtonsAsFlags()
    )
}