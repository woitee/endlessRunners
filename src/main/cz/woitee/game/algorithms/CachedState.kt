package cz.woitee.game.algorithms

import cz.woitee.game.GameState

data class CachedState(val playerX: Double, val playerY: Double, val playerYSpeed: Double, val heldActionFlags: Int) {
    constructor (gameState: GameState): this(
            gameState.player.x,
            gameState.player.y,
            gameState.player.yspeed,
            gameState.currentHeldActionsAsFlags()
    )
}