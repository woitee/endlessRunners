package game.descriptions

import game.collisions.BaseCollisionHandler
import game.collisions.collisionEffects.ApplyGameEffect
import game.collisions.collisionEffects.DestroyOther
import game.collisions.collisionEffects.composite.ConditionalCollisionEffect
import game.actions.ChangeColorAction
import game.actions.ChangeShapeAction
import game.actions.abstract.GameAction
import game.actions.JumpAction
import game.actions.composite.ConditionalAction
import game.conditions.PlayerHasColor
import game.conditions.PlayerTouchingObject
import game.effects.GameOver
import game.objects.CustomBlock
import game.objects.GameObjectClass
import game.objects.GameObjectColor
import geom.Direction4

/**
 * Created by woitee on 23/07/2017.
 */
class BitTripGameDescription: GameDescription() {
    val trampolineAction = ConditionalAction(
        PlayerTouchingObject(Direction4.DOWN, GameObjectClass.CUSTOM0),
        JumpAction(30.0)
    )

    override val allActions: List<GameAction> = listOf(
        JumpAction(22.0),
        ChangeShapeAction(2, 1),
        trampolineAction,
        ChangeColorAction(GameObjectColor.YELLOW)
    )
    override val customObjects = arrayListOf(CustomBlock(0), CustomBlock(1))

    override val collisionEffects = mapOf(
        Pair(
            BaseCollisionHandler.CollisionHandlerEntry(
                GameObjectClass.PLAYER,
                GameObjectClass.CUSTOM1,
                Direction4.any()
            ),
            ConditionalCollisionEffect(
                PlayerHasColor(GameObjectColor.YELLOW),
                DestroyOther(),
                ApplyGameEffect(GameOver())
            )
        )
    )
}