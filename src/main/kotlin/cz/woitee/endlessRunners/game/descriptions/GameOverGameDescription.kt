package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ApplyGameEffectAction
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.effects.GameOver

class GameOverGameDescription : GameDescription() {
    override val allActions: List<GameButtonAction> = listOf(
        ApplyGameEffectAction(GameOver())
    )
}
