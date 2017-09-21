package game.gameDescriptions

import game.gameActions.ChangeShapeAction
import game.gameActions.abstract.GameAction
import game.gameActions.JumpAction
import game.gameActions.composite.ConditionalAction
import game.gameConditions.PlayerTouchingObject
import game.gameObjects.CustomBlock
import game.gameObjects.GameObjectClass
import geom.Direction4

/**
 * Created by woitee on 23/07/2017.
 */
class BitTripGameDescription: GameDescription() {
    val trampolineAction = ConditionalAction(
        PlayerTouchingObject(Direction4.DOWN, GameObjectClass.CUSTOM0),
        JumpAction(30.0)
    )

    override val allActions: List<GameAction> = listOf(JumpAction(22.0), ChangeShapeAction(2, 1), trampolineAction)
    override val customObjects = arrayListOf(CustomBlock(0))
}