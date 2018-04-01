package cz.woitee.game.descriptions

import cz.woitee.game.actions.GameOverAction
import cz.woitee.game.actions.abstract.GameButtonAction

class GameOverGameDescription: GameDescription() {
    override val allActions: List<GameButtonAction> = listOf(
        GameOverAction()
    )
}