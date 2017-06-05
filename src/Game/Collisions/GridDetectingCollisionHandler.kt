package Game.Collisions

import Game.Game
import Game.GameState
import Game.BlockHeight
import Game.BlockWidth
import Geom.direction4

/**
 * Created by woitee on 05/06/2017.
 */

class GridDetectingCollisionHandler(game: Game): BaseCollisionHandler(game) {
    override fun nearestCollision(gameState: GameState, ax: Double, ay: Double, bx: Double, by: Double): Collision? {
        val gridsBetween = gameState.gridLocationsBetween(ax, ay, bx, by)
        val velocityX = ax - bx
        val velocityY = ay - by
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
}