package Game.PlayerControllers

import Game.GameState

/**
 * Created by woitee on 13/01/2017.
 */

interface IPlayerController {
    fun onUpdate(gameState: GameState)
}