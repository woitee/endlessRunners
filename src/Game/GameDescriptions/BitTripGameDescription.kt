package Game.GameDescriptions

import Game.GameActions.ChangeShape
import Game.GameActions.IGameAction
import Game.GameActions.JumpAction

/**
 * Created by woitee on 23/07/2017.
 */
class BitTripGameDescription: GameDescription() {
    override val allActions: List<IGameAction> = listOf(JumpAction(22.0), ChangeShape(2, 1))
}