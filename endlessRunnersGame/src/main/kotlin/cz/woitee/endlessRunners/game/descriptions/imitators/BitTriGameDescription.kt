package cz.woitee.endlessRunners.game.descriptions.imitators

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
import cz.woitee.endlessRunners.game.collisions.collisionEffects.composite.MultipleCollisionEffect
import cz.woitee.endlessRunners.game.conditions.PlayerHasColor
import cz.woitee.endlessRunners.game.conditions.PlayerTouchingObject
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.effects.GameOver
import cz.woitee.endlessRunners.game.effects.ScoreChange
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.objects.GameObjectColor
import cz.woitee.endlessRunners.geom.Direction4

/**
 * Imitation of a Bit.Trip Runner game.
 */
open class BitTriGameDescription : GameDescription() {
    val trampolineAction = ConditionalAction(
        PlayerTouchingObject(Direction4.DOWN, GameObjectClass.CUSTOM0),
        JumpAction(25.0)
    )

    override val allActions: ArrayList<GameAction> = arrayListOf(
        JumpAction(17.0),
        ChangeShapeHoldAction(2, 1),
        trampolineAction,
        ChangeColorHoldAction(GameObjectColor.YELLOW)
    )
    override val customObjects = arrayListOf<GameObject>(CustomBlock(0), CustomBlock(1), CustomBlock(2), CustomBlock(3))

    override val collisionEffects = hashMapOf<BaseCollisionHandler.CollisionHandlerEntry, ICollisionEffect>(
        // Collision with red block results in death
        Pair(
            BaseCollisionHandler.CollisionHandlerEntry(
                GameObjectClass.PLAYER,
                GameObjectClass.CUSTOM1,
                Direction4.any()
            ),
            ApplyGameEffect(GameOver())
        ),
        // Yellow block is coin -> gain score
        Pair(
            BaseCollisionHandler.CollisionHandlerEntry(
                GameObjectClass.PLAYER,
                GameObjectClass.CUSTOM2,
                Direction4.any()
            ),
            MultipleCollisionEffect(ApplyGameEffect(ScoreChange(100)), DestroyOther())
        ),
        // Orange block is a door, which can be kicked through
        Pair(
            BaseCollisionHandler.CollisionHandlerEntry(
                GameObjectClass.PLAYER,
                GameObjectClass.CUSTOM3,
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
