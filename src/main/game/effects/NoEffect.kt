package game.effects

import game.GameState

object NoEffect: GameEffect() {
    override fun applyOn(gameState: GameState) {
        // do nothing
    }
}