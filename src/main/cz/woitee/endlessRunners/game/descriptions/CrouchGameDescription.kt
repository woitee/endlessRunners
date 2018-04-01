package cz.woitee.game.descriptions

import cz.woitee.game.actions.ChangeShapeAction
import cz.woitee.game.actions.JumpAction
import cz.woitee.game.actions.abstract.GameButtonAction

open class CrouchGameDescription: GameDescription() {
    override val allActions: List<GameButtonAction> = listOf(
            JumpAction(22.0),
            ChangeShapeAction(2, 1)
    )
}