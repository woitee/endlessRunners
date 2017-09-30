package cz.woitee.game.effects

import cz.woitee.game.GameState

object NoEffect: GameEffect() {
    override fun applyOn(gameState: GameState) {
        // do nothing
    }
}