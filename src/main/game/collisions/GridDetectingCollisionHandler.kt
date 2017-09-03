package game.collisions

import game.Game
import game.GameState
import game.BlockHeight
import game.BlockWidth
import geom.direction4
import geom.twoNumbers2Direction4

/**
 * A collision detector which uses the grid as a starting point to look for collisions.
 * It detects all grid locations through which the line goes and checks if there are collisions in them.
 *
 * Created by woitee on 05/06/2017.
 */

class GridDetectingCollisionHandler(game: Game): BaseCollisionHandler(game) {
    override fun nearestCollision(gameState: GameState, ax: Double, ay: Double, bx: Double, by: Double): Collision? {
        return nearestByGridLocations(gameState, ax, ay, bx, by)
    }

    /**
     * Unoptimized version of the search, but clearer to read. It uses the method to find object from GameState,
     * and initializes an arraylist to hold the locations (also objects) in.
     */
    fun unoptimizedVersion(gameState: GameState, ax: Double, ay: Double, bx: Double, by: Double): Collision? {
        val gridsBetween = gameState.gridLocationsBetween(ax, ay, bx, by)
        val velocityX = ax - bx
        val velocityY = ay - by
        if (gridsBetween.count() == 1) {
            val gameObject = gameState.grid[gridsBetween[0]]
            return if (gameObject?.isSolid ?: false) {
                Collision(gameObject!!, ax, ay, ax, ay, twoNumbers2Direction4(bx - ax, by - ay))
            } else {
                null
            }
        }
        for (i in 1 .. gridsBetween.lastIndex) {
            val gridLoc = gridsBetween[i]
            if (gameState.grid[gridLoc]?.isSolid == true) {
                // We found collision
                // Direction is given by difference from last block
                val dir = gridLoc - gridsBetween[i-1]
                val colX: Double
                val colY: Double
                if (dir.y == 0) {
                    colX = ((if (dir.x > 0) gridLoc.x else gridLoc.x + 1) * BlockWidth).toDouble()
                    val ratio = (colX - ax) / velocityX
                    colY = ratio * velocityY + ay
                } else {
                    colY = ((if (dir.y > 0) gridLoc.y else gridLoc.y + 1) * BlockHeight).toDouble()
                    val ratio = (colY - ay) / velocityY
                    colX = ratio * velocityX + ax
                }
                return Collision(gameState.grid[gridLoc]!!, colX, colY, ax, ay, dir.direction4())
            }
        }
        return null
    }

    /**
     * Slightly faster version of finding nearest collision. Doesn't use any arraylists and Point objects, calls the
     * functionality directly.
     */
    fun nearestByGridLocations(gameState: GameState, ax: Double, ay: Double, bx: Double, by: Double): Collision? {
        // Assume a is lefter than b (has less Y)
        // Player usually runs left to right, exceptional situations run through the unoptimized version
        if (bx < ax) {
            return unoptimizedVersion(gameState, ax, ay, bx, by)
        }

        fun autoRange(a: Int, b: Int): IntProgression {
            return if (a <= b) a..b else a downTo b
        }

        val aGridX = ax.toInt() / BlockWidth - gameState.gridX
        val aGridY = ay.toInt() / BlockWidth
        val bGridX = bx.toInt() / BlockWidth - gameState.gridX
        val bGridY = by.toInt() / BlockWidth
        if (aGridX == bGridX && aGridY == bGridY) {
            // no change of grid location -> check if collision at this one spot
            val gameObject = gameState.grid[aGridX, aGridY]
            return if (gameObject?.isSolid ?: false) {
                Collision(gameObject!!, ax, ay, ax, ay, twoNumbers2Direction4(bx - ax, by - ay))
            } else {
                null
            }
        }
        val dirY = (by - ay) / (bx - ax)

        // We proceed by strips of vertical blocks - lastGrid means list stripe, prevGrid means really previous
        var lastGridY = aGridY
        var prevGridY = aGridY
        var prevGridX = aGridX
        var skippedFirst = false
        // we'll go by vertical
        for (gridX in aGridX .. bGridX - 1) {
            val borderX = (gameState.gridX + gridX + 1) * BlockWidth
            val contactY = ay + (borderX - ax) * dirY
            val curGridY = (contactY / BlockHeight).toInt()
            for (gridY in autoRange(lastGridY, curGridY)) {
                if (!skippedFirst) {
                    skippedFirst = true
                } else if (gameState.grid[gridX, gridY]?.isSolid == true) {
                    return collisionFromGridLoc(gameState, ax, ay, bx, by, gridX, gridY, gridX - prevGridX, gridY - prevGridY)
                }
                prevGridX = gridX
                prevGridY = gridY
            }
            lastGridY = curGridY
        }
        for (gridY in autoRange(lastGridY, bGridY)) {
            if (!skippedFirst) {
                skippedFirst = true
            } else if (gameState.grid[bGridX, gridY]?.isSolid == true) {
                return collisionFromGridLoc(gameState, ax, ay, bx, by, bGridX, gridY, bGridX - prevGridX, gridY - prevGridY)
            }
            prevGridX = bGridX
            prevGridY = gridY
        }
        return null
    }

    /**
     * Helper method, creates a Collision object from given parameters.
     */
    fun collisionFromGridLoc(gameState: GameState, ax: Double, ay: Double, bx: Double, by: Double, gridX: Int, gridY: Int, relX: Int, relY: Int): Collision {
        val colX: Double
        val colY: Double
        val velocityX = ax - bx
        val velocityY = ay - by
        if (relY == 0) {
            colX = (((if (relX > 0) gridX else gridX + 1) + gameState.gridX) * BlockWidth).toDouble()
            val ratio = (colX - ax) / velocityX
            colY = ratio * velocityY + ay
        } else {
            colY = ((if (relY > 0) gridY else gridY + 1) * BlockHeight).toDouble()
            val ratio = (colY - ay) / velocityY
            colX = ratio * velocityX + ax
        }
        return Collision(gameState.grid[gridX, gridY]!!, colX, colY, ax, ay, twoNumbers2Direction4(relX, relY))
    }
}