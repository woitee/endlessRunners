package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.GameOverAction
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction

class GameOverGameDescription: GameDescription() {
    override val allActions: List<GameButtonAction> = listOf(
        GameOverAction()
    )
}