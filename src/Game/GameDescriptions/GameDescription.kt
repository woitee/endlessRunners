package Game.GameDescriptions

import Game.BlockHeight
import Game.GameActions.*
import Game.GameEffects.*
import Game.Collisions.CollisionEffects.*
import Game.Collisions.BaseCollisionHandler.CollisionHandlerEntry
import Game.GameObjects.GameObjectClass
import Geom.Direction4

/**
 * This class contains the "Genotype" or "Settings" of the game. It contains all the possible blocks, actions and effects,
 * and other nuances that differ this game from the basics.
 *
 * It doesn't contain any information about the game levels or level generation.
 *
 * Default constructor creates default settings of the game.
 *
 * Created by woitee on 16/01/2017.
 */

open class GameDescription {
    open val playerStartingSpeed = 12.0
    open val allActions = listOf<GameAction>(JumpAction(22.0))
    open val permanentEffects = listOf(Gravity(GameEffect.Target.PLAYER, 100 * 0.7 / BlockHeight))
    open val collisionEffects = mapOf(
        Pair(
            CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.SOLIDBLOCK, Direction4.DOWN or Direction4.UP),
            MoveToContact()
        ),
        Pair(
            CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.SOLIDBLOCK, Direction4.RIGHT),
            ApplyGameEffect(GameOver())
        )
    )
}