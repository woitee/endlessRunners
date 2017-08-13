package game.gameDescriptions

import game.BlockHeight
import game.Game
import game.gameActions.*
import game.gameEffects.*
import game.collisions.collisionEffects.*
import game.collisions.BaseCollisionHandler.CollisionHandlerEntry
import game.gameActions.abstract.GameAction
import game.gameObjects.GameObject
import game.gameObjects.GameObjectClass
import game.gameObjects.Player
import game.gameObjects.SolidBlock
import geom.Direction4
import java.util.*

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
    /**
     * Every game implicitly uses Player and SolidBlock.
     */
    open val customObjects: List<GameObject> = ArrayList<GameObject>()
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

    /**
     * Returns all gameObjects used in this game.
     */
    val allObjects: List<GameObject>
        get() {
            val res = ArrayList(this.customObjects)
            res.add(Player())
            res.add(SolidBlock())
            return res
        }

    protected val _charToObject = HashMap<Char, GameObject?>()
    val charToObject: Map<Char, GameObject?>
        get() {
            if (_charToObject.size == 0) {
                _charToObject[' '] = null
                for (gameObject in allObjects) {
                    _charToObject[gameObject.dumpChar] = gameObject
                }
            }
            return _charToObject
        }
}