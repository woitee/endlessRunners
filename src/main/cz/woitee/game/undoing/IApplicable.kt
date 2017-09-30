package cz.woitee.game.undoing

import cz.woitee.game.GameState

/**
 * Created by woitee on 09/04/2017.
 */

interface IApplicable {
    fun applyOn(gameState: GameState)
}