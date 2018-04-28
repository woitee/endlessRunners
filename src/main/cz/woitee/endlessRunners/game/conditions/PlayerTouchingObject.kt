package cz.woitee.endlessRunners.game.conditions

import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.game.objects.GameObjectClass
import cz.woitee.endlessRunners.geom.Direction4

class PlayerTouchingObject(val dir: Direction4, val gameObjectClass: GameObjectClass?): GameCondition() {
    override fun isTrue(gameState: GameState): Boolean {
        val player = gameState.player
        if (dir == Direction4.DOWN || dir == Direction4.UP) {
            val gridLoc = gameState.gridLocation(
                player.x,
                if (dir == Direction4.DOWN) {player.y - 1} else {player.y + player.heightPx + 1}
            )
            for (i in 0 .. player.widthBlocks) {
                if (gameState.grid[gridLoc.x + i, gridLoc.y]?.gameObjectClass == gameObjectClass)
                    return true
            }
        } else if (dir == Direction4.RIGHT || dir == Direction4.LEFT) {
            val gridLoc = gameState.gridLocation(
                    if (dir == Direction4.LEFT) {player.x - 1} else {player.x + player.widthPx + 1},
                    player.y
            )
            for (i in 0 .. player.heightBlocks) {
                if (gameState.grid[gridLoc.x, gridLoc.y + i]?.gameObjectClass == gameObjectClass)
                    return true
            }
        } else {
            println("ERROR: Check of touching object in direction " + dir.name)
            return false
        }
        return false
    }
}