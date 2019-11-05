package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.geom.Direction4

/**
 * A condition that is true whether the player is touching a specific class of object on one of his sides.
 *
 * @param dir Direction from the player, where we check for the presence of an object.
 * @param gameObjectClass The specific class of the object to check for.
 */
class PlayerTouchingObject(val dir: Direction4, val gameObjectClass: GameObjectClass?) : GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        val player = gameState.player
        if (dir == Direction4.DOWN || dir == Direction4.UP) {
            val gridLoc = gameState.gridLocation(
                player.x,
                if (dir == Direction4.DOWN) { player.y - 1 } else { player.y + player.heightPx + 1 }
            )
            for (i in 0 .. player.widthBlocks) {
                if (gameState.grid.safeGet(gridLoc.x + i, gridLoc.y)?.gameObjectClass == gameObjectClass)
                    return true
            }
        } else if (dir == Direction4.RIGHT || dir == Direction4.LEFT) {
            val gridLoc = gameState.gridLocation(
                    if (dir == Direction4.LEFT) { player.x - 1 } else { player.x + player.widthPx + 1 },
                    player.y
            )
            for (i in 0 .. player.heightBlocks) {
                if (gameState.grid.safeGet(gridLoc.x, gridLoc.y + i)?.gameObjectClass == gameObjectClass)
                    return true
            }
        } else {
            println("ERROR: Check of touching object in direction " + dir.name)
            return false
        }
        return false
    }

    override fun toString(): String {
        return "PlayerTouchingObject($gameObjectClass)"
    }
}
