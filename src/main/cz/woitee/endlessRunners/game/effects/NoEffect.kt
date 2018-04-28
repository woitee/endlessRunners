package cz.woitee.endlessRunners.game.effects

import cz.woitee.endlessRunners.game.GameState

object NoEffect: GameEffect() {
    override fun applyOn(gameState: GameState) {
        // do nothing
    }
}