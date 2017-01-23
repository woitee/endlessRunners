package Game.GameEffects

import Game.GameState

/**
 * Created by woitee on 23/01/2017.
 */

class GameOver(): GameEffect() {
    override fun apply(gameState: GameState) {
        println("-------------")
        println("| GAME OVER |")
        println("-------------")
    }
}