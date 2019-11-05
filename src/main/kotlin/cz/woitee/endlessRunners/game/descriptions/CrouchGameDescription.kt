package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ChangeShapeAction
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction

open class CrouchGameDescription: GameDescription() {
    override val allActions: List<GameButtonAction> = listOf(
            JumpAction(22.0),
            ChangeShapeAction(2, 1)
    )
}