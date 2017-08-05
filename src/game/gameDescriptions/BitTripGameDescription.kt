package game.gameDescriptions

import game.gameActions.ChangeShapeAction
import game.gameActions.GameAction
import game.gameActions.JumpAction

/**
 * Created by woitee on 23/07/2017.
 */
class BitTripGameDescription: GameDescription() {
    override val allActions: List<GameAction> = listOf(JumpAction(22.0), ChangeShapeAction(2, 1))
}