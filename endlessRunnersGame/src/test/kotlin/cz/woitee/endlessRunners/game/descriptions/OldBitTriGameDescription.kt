package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ChangeColorHoldAction
import cz.woitee.endlessRunners.game.actions.ChangeShapeHoldAction
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.actions.composite.ConditionalAction
import cz.woitee.endlessRunners.game.collisions.BaseCollisionHandler
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ApplyGameEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.DestroyOther
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ICollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.composite.ConditionalCollisionEffect
import cz.woitee.endlessRunners.game.conditions.PlayerHasColor
import cz.woitee.endlessRunners.game.conditions.PlayerTouchingObject
import cz.woitee.endlessRunners.game.effects.GameOver
import cz.woitee.endlessRunners.game.effects.ScoreChange
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.objects.GameObjectColor
import cz.woitee.endlessRunners.geom.Direction4

/**
 * A historical version of BitTriGameDescription, that is used in several automatic tests.
 * Only needed for the purpose of testing.
 */
open class OldBitTriGameDescription : GameDescription() {
    val trampolineAction = ConditionalAction(
            PlayerTouchingObject(Direction4.DOWN, GameObjectClass.CUSTOM0),
            JumpAction(30.0)
    )

    override val allActions: ArrayList<GameAction> = arrayListOf(
            JumpAction(22.0),
            ChangeShapeHoldAction(2, 1),
            trampolineAction,
            ChangeColorHoldAction(GameObjectColor.YELLOW)
    )
    override val customObjects = arrayListOf<GameObject>(CustomBlock(0), CustomBlock(1))

    override val collisionEffects = hashMapOf<BaseCollisionHandler.CollisionHandlerEntry, ICollisionEffect>(
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
