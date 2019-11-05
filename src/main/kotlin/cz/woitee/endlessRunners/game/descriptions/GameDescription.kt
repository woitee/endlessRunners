package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameAction
import cz.woitee.endlessRunners.game.collisions.BaseCollisionHandler.CollisionHandlerEntry
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ICollisionEffect
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.Gravity
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.Player
import cz.woitee.endlessRunners.game.objects.SolidBlock
import cz.woitee.endlessRunners.utils.CopyUtils
import java.io.Serializable
import java.util.*

/**
 * This class contains the "Settings" of the game. It contains all the possible blocks, actions and effects,
 * and other nuances that differ this game from the basics.
 *
 * It doesn't contain any information about the game levels or level generation.
 *
 * Default constructor creates default settings of a game.
 */

open class GameDescription : Serializable {
    /**
     * Every game implicitly uses Player and SolidBlock.
     */
    open val customObjects: ArrayList<GameObject> = ArrayList()
    open var playerStartingSpeed = 12.0
    open val allActions = arrayListOf<GameAction>(JumpAction(22.0))
    open val permanentEffects = arrayListOf<GameEffect>(Gravity(GameEffect.Target.PLAYER, 100 * 0.7 / BlockHeight))
    open val collisionEffects = hashMapOf<CollisionHandlerEntry, ICollisionEffect>(
//  This setting is now default for any collision with SolidBlock, and that without needing to be set here.
//  It can however be overriden in descendant classes to be set differently.
//        Pair(
//            CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.SOLIDBLOCK, Direction4.DOWN or Direction4.UP),
//            MoveToContact()
//        ),
//        Pair(
//            CollisionHandlerEntry(GameObjectClass.PLAYER, GameObjectClass.SOLIDBLOCK, Direction4.RIGHT),
//            ApplyGameEffect(GameOver())
//        )
    )

    /**
     * Returns all gameObjects used in this game.
     */
    val allObjects: List<GameObject>
        get() {
            val res = ArrayList(this.customObjects)
            res.add(Player(0.0, 0.0))
            res.add(SolidBlock())
            return res
        }

    @Transient
    protected val _charToObject = ThreadLocal.withInitial { HashMap<Char, GameObject?>() }
    val charToObject: Map<Char, GameObject?>
        get() {
            val threadCharToObject = _charToObject.get()
            if (threadCharToObject.size == 0) {
                threadCharToObject[' '] = null
                for (gameObject in allObjects) {
                    threadCharToObject[gameObject.dumpChar] = gameObject
                }
            }

            return threadCharToObject
        }

    fun makeCopy(): GameDescription {
        return CopyUtils.copyByJavaSerialization(this)
    }
}
