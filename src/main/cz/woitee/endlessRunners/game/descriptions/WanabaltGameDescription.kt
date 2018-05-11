package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.actions.ApplyGameEffectAction
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.SpeedChange

/**
 * Created by woitee on 23/07/2017.
 */
open class WanabaltGameDescription: GameDescription() {
    val boostEffect = SpeedChange(GameEffect.Target.PLAYER, 20.0, timeout = 1.0)

    override val allActions: List<GameButtonAction> = listOf<GameButtonAction>(
            JumpAction(22.0),
            ApplyGameEffectAction(boostEffect)
    )
//    override val customObjects = arrayListOf(CustomBlock(0), CustomBlock(1))

//    override val collisionEffects = mapOf(
//            Pair(
//                    BaseCollisionHandler.CollisionHandlerEntry(
//                            GameObjectClass.PLAYER,
//                            GameObjectClass.CUSTOM1,
//                            Direction4.any()
//                    ),
//                    ConditionalCollisionEffect(
//                            PlayerHasColor(GameObjectColor.YELLOW),
//                            DestroyOther(),
//                            ApplyGameEffect(GameOver())
//                    )
//            )
//    )
}