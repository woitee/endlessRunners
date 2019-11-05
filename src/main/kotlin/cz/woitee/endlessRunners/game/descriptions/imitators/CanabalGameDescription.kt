package cz.woitee.endlessRunners.game.descriptions.imitators

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.collisions.BaseCollisionHandler
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ApplyGameEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.DestroyOther
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ICollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.composite.MultipleCollisionEffect
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.Gravity
import cz.woitee.endlessRunners.game.effects.SpeedChange
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.geom.Direction4

/**
 * Imitation of Canabalt.
 */
open class CanabalGameDescription : GameDescription() {
    override val allActions: ArrayList<GameAction> = arrayListOf(
            JumpAction(25.0),
            JumpAction(18.0)
    )
    override val customObjects = arrayListOf<GameObject>(CustomBlock(0))
    override val permanentEffects: ArrayList<GameEffect> = arrayListOf(
            Gravity(GameEffect.Target.PLAYER, 100 * 0.7 / BlockHeight),
            SpeedChange(GameEffect.Target.PLAYER, 0.03, GameEffect.Relativity.RELATIVE)
    )

    override val collisionEffects = hashMapOf<BaseCollisionHandler.CollisionHandlerEntry, ICollisionEffect>(
            Pair(
                    BaseCollisionHandler.CollisionHandlerEntry(
                            GameObjectClass.PLAYER,
                            GameObjectClass.CUSTOM0,
                            Direction4.any()
                    ),
                    MultipleCollisionEffect(
                        DestroyOther(),
                        ApplyGameEffect(SpeedChange(GameEffect.Target.PLAYER, -4.0, GameEffect.Relativity.RELATIVE))
                    )
            )
    )
}
