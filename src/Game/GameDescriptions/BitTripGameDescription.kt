package Game.GameDescriptions

import Game.GameActions.ChangeShapeAction
import Game.GameActions.GameAction
import Game.GameActions.JumpAction

/**
 * Created by woitee on 23/07/2017.
 */
class BitTripGameDescription: GameDescription() {
    override val allActions: List<GameAction> = listOf(JumpAction(22.0), ChangeShapeAction(2, 1))
}