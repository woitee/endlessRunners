package cz.woitee.endlessRunners.game.descriptions.imitators

import cz.woitee.endlessRunners.game.actions.ChangeColorHoldAction
import cz.woitee.endlessRunners.game.actions.MultiJumpAction
import cz.woitee.endlessRunners.game.collisions.BaseCollisionHandler
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ApplyGameEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ICollisionEffect
import cz.woitee.endlessRunners.game.collisions.collisionEffects.MoveToContact
import cz.woitee.endlessRunners.game.collisions.collisionEffects.composite.ConditionalCollisionEffect
import cz.woitee.endlessRunners.game.conditions.PlayerHasColor
import cz.woitee.endlessRunners.game.descriptions.GameDescription
import cz.woitee.endlessRunners.game.effects.GameOver
import cz.woitee.endlessRunners.game.objects.CustomBlock
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.game.objects.GameObjectColor
import cz.woitee.endlessRunners.geom.Direction4

/**
 * Imitation of Chameleon Run.
 */
class ChameleonGameDescription : GameDescription() {
    override val customObjects = arrayListOf<GameObject>(CustomBlock(0))
    override var playerStartingSpeed = 20.0

    override val allActions = arrayListOf(
            MultiJumpAction(25.0),
            MultiJumpAction(20.0),
            ChangeColorHoldAction(GameObjectColor.GREEN, true)
    )

    override val collisionEffects = hashMapOf<BaseCollisionHandler.CollisionHandlerEntry, ICollisionEffect>(
            // Collision with solid blocks up-down survives only if player is blue
            Pair(
                    BaseCollisionHandler.CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.SOLIDBLOCK, Direction4.UP or Direction4.DOWN),
                    ConditionalCollisionEffect(PlayerHasColor(GameObjectColor.BLUE), MoveToContact(), ApplyGameEffect(GameOver()))
            ),
            // Likewise, collision with green blocks survives only if the player is green
            Pair(
                    BaseCollisionHandler.CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.CUSTOM0, Direction4.UP or Direction4.DOWN),
                    ConditionalCollisionEffect(PlayerHasColor(GameObjectColor.GREEN), MoveToContact(), ApplyGameEffect(GameOver()))
            ),
            // Colliding in direction right for both objects is death regardless of player color
            Pair(
                    BaseCollisionHandler.CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.SOLIDBLOCK, Direction4.RIGHT),
                    ApplyGameEffect(GameOver())
            ),
            Pair(
                    BaseCollisionHandler.CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.CUSTOM0, Direction4.RIGHT),
                    ApplyGameEffect(GameOver())
            )
    )
}
