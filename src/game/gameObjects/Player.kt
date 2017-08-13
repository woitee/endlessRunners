package game.gameObjects

/**
 * Created by woitee on 13/01/2017.
 */

class Player(x: Double = 0.0, y:Double = 0.0): MovingObject(x, y) {
    override val gameObjectClass = GameObjectClass.PLAYER

    override val defaultHeightBlocks = 2
    override var heightBlocks = 2

    override val dumpChar = 'P'

    fun positionOnScreen(): Double {
        return this.x - (gameState.gridX * game.BlockWidth)
    }

    override fun shallowCopy(): Player {
        return Player(this.x, this.y)
    }
}