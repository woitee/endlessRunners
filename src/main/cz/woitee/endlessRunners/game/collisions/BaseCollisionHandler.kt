package cz.woitee.game.collisions

import java.util.*

import cz.woitee.game.objects.*
import cz.woitee.game.Game
import cz.woitee.game.GameState
import cz.woitee.game.collisions.collisionEffects.ICollisionEffect
import cz.woitee.game.collisions.collisionEffects.IUndoableCollisionEffect
import cz.woitee.game.collisions.collisionEffects.MoveToContact
import cz.woitee.game.collisions.collisionEffects.ApplyGameEffect
import cz.woitee.game.undoing.IUndo
import cz.woitee.game.effects.GameOver

import cz.woitee.game.BlockHeight
import cz.woitee.game.BlockWidth
import cz.woitee.game.objects.GameObject
import cz.woitee.game.objects.GameObjectClass
import cz.woitee.game.objects.MovingObject
import cz.woitee.geom.Direction4
import cz.woitee.geom.Distance2D
import cz.woitee.geom.Vector2Double
import cz.woitee.geom.flagsToDirections
import cz.woitee.geom.*


/**
 * Class that deals with all collision detection related stuff.
 *
 * Created by woitee on 23/01/2017.
 */

open class BaseCollisionHandler(val game: Game) {
    // How many collisions with one object should be handled at most
    val MAX_COLLISIONS = 10

    data class CollisionHandlerEntry(val srcClass: GameObjectClass, val targetClass: GameObjectClass, val directionFlags: Int) {
        constructor (srcClass: GameObjectClass, targetClass: GameObjectClass, direction4: Direction4): this(srcClass, targetClass, direction4.value)
    }
    val collisionHandlerMapping = HashMap<CollisionHandlerEntry, ICollisionEffect>()
    init {
        // unwrap direction flags
        for ((entry, collEffect) in game.gameDescription.collisionEffects) {
            for (dir in entry.directionFlags.flagsToDirections()) {
                collisionHandlerMapping.put(
                        CollisionHandlerEntry(entry.srcClass, entry.targetClass, dir),
                    collEffect
                )
            }
        }
    }

    fun nearestCollision(gameState: GameState, a: Vector2Double, b: Vector2Double): Collision? {
        return nearestCollision(gameState, a.x, a.y, b.x, b.y)
    }
    open fun nearestCollision(gameState: GameState, ax: Double, ay: Double, bx: Double, by: Double): Collision? {
        val max = { a: Double, b: Double -> if (a > b) a else b }
        val min = { a: Double, b: Double -> if (a > b) b else a }

        val vecX = bx - ax
        val vecY = by - ay
        val vecXi = 1 / vecX
        val vecYi = 1 / vecY

        var collX = Double.NaN
        var collY = Double.NaN
        var collObject: GameObject? = null
        var minCollDist = Double.POSITIVE_INFINITY
        val epsilon = 0.0001

        for (gameObject in gameState.gameObjects) {
            if (!gameObject.isSolid)
                continue
            var minPart = 0.0
            var maxPart = 1.0

            if (vecX != 0.0) {
                val xPart1 = (gameObject.x - ax) * vecXi
                val xPart2 = (gameObject.x + gameObject.widthPx - ax) * vecXi

                minPart = max(minPart, min(xPart1, xPart2))
                maxPart = min(maxPart, max(xPart1, xPart2))
            } else {
                if (ax > gameObject.x + gameObject.widthPx || ax < gameObject.x)
                    continue
            }
            if (vecY != 0.0) {
                val yPart1 = (gameObject.y - ay) * vecYi
                val yPart2 = (gameObject.y + gameObject.heightPx - ay) * vecYi

                minPart = max(minPart, min(yPart1, yPart2))
                maxPart = min(maxPart, max(yPart1, yPart2))
            } else {
                if (ay > gameObject.y + gameObject.heightPx - epsilon || ay < gameObject.y)
                    continue
            }

            if (minPart >= maxPart)
                continue

            val thisCollX = ax + minPart * vecX
            val thisCollY = ay + minPart * vecY
            val dist = Distance2D.distance(ax, ay, thisCollX, thisCollY)
            if (dist < minCollDist) {
                minCollDist = dist
                collX = thisCollX
                collY = thisCollY
                collObject = gameObject
            }
        }
        if (minCollDist == Double.POSITIVE_INFINITY)
            return null

        return Collision(collObject!!, collX, collY, ax, ay)
    }

    fun getCollision(movingObject: MovingObject): Collision? {
        var closest = Double.MAX_VALUE
        var res: Collision? = null
        for (collPoint in movingObject.collPoints) {
//            val targetPoint = corner + Vector2Double(movingObject.xspeed * game.updateTime * BlockWidth, movingObject.yspeed * game.updateTime * BlockHeight)
            val collRes = nearestCollision(
                    movingObject.gameState,
                    collPoint.x,
                    collPoint.y,
                    collPoint.x + movingObject.xspeed * game.updateTime * BlockWidth,
                    collPoint.y + movingObject.yspeed * game.updateTime * BlockHeight) ?: continue
            val dist = Distance2D.distance(collRes.locationX, collRes.locationY, collPoint.x, collPoint.y)
            if (dist < closest) {
                closest = dist
                res = collRes
            }
        }
        return res
    }

    fun handleCollisions(movingObject: MovingObject) {
        _handleCollisions(movingObject)
    }
    fun handleCollisionsUndoable(movingObject: MovingObject): ArrayList<IUndo> {
        val undoList = ArrayList<IUndo>()
        _handleCollisions(movingObject, true, undoList)
        return undoList
    }
    private fun _handleCollisions(movingObject: MovingObject, undoable: Boolean = false, undoList: MutableList<IUndo>? = null) {
        for (i in 1 .. MAX_COLLISIONS) {
            val collision = getCollision(movingObject) ?: return

            var collEffect: ICollisionEffect? =
                    collisionHandlerMapping.get(CollisionHandlerEntry(
                            movingObject.gameObjectClass,
                            collision.other.gameObjectClass,
                            collision.direction
                    )) ?: getDefaultCollisionEffect(collision.other, collision.direction)
            collEffect ?: continue

            if (!undoable) {
                collEffect.apply(movingObject, collision)
            } else {
                val undoableEffect = collEffect as IUndoableCollisionEffect
                val undo = undoableEffect.applyUndoable(movingObject, collision)
                undoList!!.add(undo)
            }

            if (movingObject.gameState.isGameOver) return
        }
        println("Collision limit ($MAX_COLLISIONS}) reached with object ${movingObject}! Maybe collision handling is done improperly?")
    }

    protected fun getDefaultCollisionEffect(other: GameObject, direction: Direction4): ICollisionEffect? {
        return if (other.isSolid) {
            if (direction == Direction4.UP || direction == Direction4.DOWN) {
                MoveToContact()
            } else {
                ApplyGameEffect(GameOver())
            }
        } else {
            null
        }
    }

}