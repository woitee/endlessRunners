package cz.woitee.game.descriptions

import cz.woitee.game.collisions.BaseCollisionHandler
import cz.woitee.game.collisions.collisionEffects.ApplyGameEffect
import cz.woitee.game.collisions.collisionEffects.DestroyOther
import cz.woitee.game.collisions.collisionEffects.composite.ConditionalCollisionEffect
import cz.woitee.game.actions.ChangeColorAction
import cz.woitee.game.actions.ChangeShapeAction
import cz.woitee.game.actions.abstract.GameAction
import cz.woitee.game.actions.JumpAction
import cz.woitee.game.actions.composite.ConditionalAction
import cz.woitee.game.conditions.PlayerHasColor
import cz.woitee.game.conditions.PlayerTouchingObject
import cz.woitee.game.effects.GameOver
import cz.woitee.game.objects.CustomBlock
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.GameObjectColor
import cz.woitee.geom.Direction4

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
            ChangeColorAction(GameObjectColor.YELLOW, 1.0)
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