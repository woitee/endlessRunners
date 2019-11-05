package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ApplyGameEffectAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.effects.GameOver

/**
 * A very nihilistic GameDescription, where the only thing the player can do is die at will.
 */
class GameOverGameDescription : GameDescription() {
    override val allActions: ArrayList<GameAction> = arrayListOf(
        ApplyGameEffectAction(GameOver())
    )
}
