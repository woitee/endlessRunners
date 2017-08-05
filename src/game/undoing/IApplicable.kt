package game.undoing

import game.GameState

/**
 * Created by woitee on 09/04/2017.
 */

interface IApplicable {
    fun applyOn(gameState: GameState)
}