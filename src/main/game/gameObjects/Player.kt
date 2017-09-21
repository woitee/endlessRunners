package game.gameObjects

import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * Created by woitee on 13/01/2017.
 */

class Player(x: Double, y:Double): MovingObject(x, y) {
    // parameterless constructor for serialization purposes
    constructor(): this(0.0, 0.0)

    override val gameObjectClass = GameObjectClass.PLAYER

    override val defaultHeightBlocks = 2
    override var heightBlocks = 2

    override val dumpChar = 'P'

    val defaultColor = GameObjectColor.BLUE
    override var color = GameObjectColor.BLUE

    fun positionOnScreen(): Double {
        return this.x - (gameState.gridX * game.BlockWidth)
    }

    override fun makeCopy(): Player {
        val player = Player(this.x, this.y)
        player.xspeed = xspeed
        player.yspeed = yspeed
        player.heightBlocks = heightBlocks
        player.widthBlocks = widthBlocks
        return player
    }
}