package cz.woitee.endlessRunners.game.collisions

import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.geom.Direction4
import cz.woitee.endlessRunners.utils.NumberUtils

object CollisionUtils {
    /**
     * Converts a collision location on a gameObject to collision direction.
     */
    fun gameObjectLocToDir(gameObject: GameObject, locationX: Double, locationY: Double): Direction4 {
        return when {
            NumberUtils.apxEquals(gameObject.x, locationX) -> Direction4.RIGHT
            NumberUtils.apxEquals(gameObject.y, locationY) -> Direction4.UP
            NumberUtils.apxEquals(gameObject.y + gameObject.heightPx, locationY) -> Direction4.DOWN
            NumberUtils.apxEquals(gameObject.x + gameObject.widthPx, locationX) -> Direction4.LEFT
            else -> Direction4.NONE
        }
    }
}
