package cz.woitee.game.playerControllers

import cz.woitee.game.GameState

/**
 * Created by woitee on 13/01/2017.
 */

abstract class PlayerController {
    abstract fun onUpdate(gameState: GameState): PlayerControllerOutput?
    open fun reset() {}
}