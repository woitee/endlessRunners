package cz.woitee.endlessRunners.game.descriptions

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.actions.JumpAction
import cz.woitee.endlessRunners.game.actions.abstract.GameButtonAction
import cz.woitee.endlessRunners.game.collisions.BaseCollisionHandler.CollisionHandlerEntry
import cz.woitee.endlessRunners.game.collisions.collisionEffects.ICollisionEffect
import cz.woitee.endlessRunners.game.effects.GameEffect
import cz.woitee.endlessRunners.game.effects.Gravity
import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.game.objects.Player
import cz.woitee.endlessRunners.game.objects.SolidBlock

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
    open val allActions = listOf<GameButtonAction>(JumpAction(22.0))
    open val permanentEffects = listOf(Gravity(GameEffect.Target.PLAYER, 100 * 0.7 / BlockHeight))
    open val collisionEffects = mapOf<CollisionHandlerEntry, ICollisionEffect>(
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
