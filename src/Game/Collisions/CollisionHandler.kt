package Game.Collisions

import java.util.*

import Game.GameObjects.*
import Game.Game
import Game.GameState
import Geom.direction4
import Geom.Vector2Double
import Game.Collisions.CollisionEffects.ICollisionEffect
import Game.Collisions.CollisionEffects.IUndoableCollisionEffect
import Game.Undoing.IUndo

import Game.BlockHeight
import Game.BlockWidth
import Geom.Direction4
import Geom.flagsToDirections


/**
 * Class that deals with all collision detection related stuff.
 *
 * Created by woitee on 23/01/2017.
 */

class CollisionHandler(val game: Game) {
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
    fun nearestCollision(gameState: GameState, ax: Double, ay: Double, bx: Double, by: Double): Collision? {
        val gridsBetween = gameState.gridLocationsBetween(ax, ay, bx, by)
        val velocity = Vector2Double(ax - bx, ay - by)
        for (i in 1 .. gridsBetween.lastIndex) {
            val gridLoc = gridsBetween[i]
            if (gameState.grid[gridLoc]?.isSolid == true) {
                // We found collision
                // Direction is given by difference from last block
                val dir = gridLoc - gridsBetween[i-1]
                val collPoint = if (dir.y == 0) {
                    val colX = ((if (dir.x > 0) gridLoc.x else gridLoc.x + 1) * BlockWidth).toDouble()
                    val ratio = (colX - ax) / velocity.x
                    val colY = ratio * velocity.y + ay
                    Vector2Double(colX, colY)
                } else {
                    val colY = ((if (dir.y > 0) gridLoc.y else gridLoc.y + 1) * BlockHeight).toDouble()
                    val ratio = (colY - ay) / velocity.y
                    val colX = ratio * velocity.x + ax
                    Vector2Double(colX, colY)
                }
                return Collision(gameState.grid[gridLoc]!!, collPoint, Vector2Double(ax, ay), dir.direction4())
            }
        }
        return null
    }

    fun getCollision(movingObject: MovingObject): Collision? {
        var closest = Double.MAX_VALUE
        var res: Collision? = null
        for (corner in movingObject.corners) {
            val targetPoint = corner + Vector2Double(movingObject.xspeed * game.updateTime * BlockWidth, movingObject.yspeed * game.updateTime * BlockHeight)
            val collRes = nearestCollision(movingObject.gameState, corner.x, corner.y, targetPoint.x, targetPoint.y) ?: continue
            val dist = collRes.location.distanceFrom(corner.x, corner.y)
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

            val collEffect: ICollisionEffect? =
                    collisionHandlerMapping.get(CollisionHandler.CollisionHandlerEntry(
                            movingObject.gameObjectClass,
                            collision.other.gameObjectClass,
                            collision.direction
                    ))
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

}