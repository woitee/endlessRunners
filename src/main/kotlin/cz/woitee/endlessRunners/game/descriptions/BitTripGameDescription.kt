package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.collisions.BaseCollisionHandler
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ApplyGameEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.DestroyOther
import cz.woitee.endlessRunners.game.collisions.collisionEffects.composite.ConditionalCollisionEffect
import cz.woitee.endlessRunners.game.actions.ChangeColorAction
import cz.woitee.endlessRunners.game.actions.ChangeShapeAction
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.composite.ConditionalAction
import cz.woitee.endlessRunners.game.conditions.PlayerHasColor
import cz.woitee.endlessRunners.game.conditions.PlayerTouchingObject
import cz.woitee.endlessRunners.game.effects.GameOver
import cz.woitee.endlessRunners.game.effects.ScoreChange
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.objects.GameObjectColor
import cz.woitee.endlessRunners.geom.Direction4

/**
 * Created by woitee on 23/07/2017.
 */
open class BitTripGameDescription: GameDescription() {
    val trampolineAction = ConditionalAction(
            PlayerTouchingObject(Direction4.DOWN, GameObjectClass.CUSTOM0),
            JumpAction(30.0)
    )

    override val allActions: List<GameButtonAction> = listOf(
            JumpAction(22.0),
            ChangeShapeAction(2, 1),
            trampolineAction,
            ChangeColorAction(GameObjectColor.YELLOW)
    )
    override val customObjects = arrayListOf(CustomBlock(0), CustomBlock(1), CustomBlock(2))

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
        ),
        Pair(
            BaseCollisionHandler.CollisionHandlerEntry(
                GameObjectClass.PLAYER,
                GameObjectClass.CUSTOM2,
                Direction4.any()
            ),
            ApplyGameEffect(ScoreChange(10))
        )
    )
}