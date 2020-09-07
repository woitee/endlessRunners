package cz.woitee.endlessRunners.game.descriptions.imitators

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.collisions.BaseCollisionHandler
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ApplyGameEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ICollisionEffect
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.GameOver
import cz.woitee.endlessRunners.game.effects.Gravity
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.geom.Direction4

class ImpossibleGameDescription : GameDescription() {
    override val customObjects = arrayListOf<GameObject>(CustomBlock(1))
    override var playerStartingSpeed = 12.0

    override val allActions = arrayListOf<GameAction>(
            JumpAction(27.5)
    )
    override val permanentEffects = arrayListOf<GameEffect>(Gravity(GameEffect.Target.PLAYER, 230 * 0.7 / BlockHeight))

    override val collisionEffects = hashMapOf<BaseCollisionHandler.CollisionHandlerEntry, ICollisionEffect>(
            // Collision with red block results in death
            Pair(
                    BaseCollisionHandler.CollisionHandlerEntry(
                            GameObjectClass.PLAYER,
                            GameObjectClass.CUSTOM1,
                            Direction4.any()
                    ),
                    ApplyGameEffect(GameOver())
            )
    )
}