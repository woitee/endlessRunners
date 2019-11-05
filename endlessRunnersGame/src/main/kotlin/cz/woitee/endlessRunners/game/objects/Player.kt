package cz.woitee.endlessRunners.game.objects

import cz.woitee.endlessRunners.game.BlockWidth
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * The player in the game.
 */

class Player(x: Double = 0.0, y: Double = 0.0) : MovingObject(x, y) {
    override val gameObjectClass = GameObjectClass.PLAYER

    override val defaultHeightBlocks = 2
    override var heightBlocks = 2

    override val dumpChar = 'P'

    val defaultColor = GameObjectColor.BLUE
    override var color = GameObjectColor.BLUE

    val minXspeed = 12.0
    val maxXspeed = 36.0

    var timesJumpedSinceTouchingGround = 0

    fun positionOnScreen(): Double {
        return this.x - (gameState.gridX * BlockWidth)
    }

    fun assertXSpeed() {
        xspeed = xspeed.coerceIn(minXspeed, maxXspeed)
    }

    override fun makeCopy(): Player {
        val player = Player(x, y)
        player.xspeed = xspeed
        player.yspeed = yspeed
        player.heightBlocks = heightBlocks
        player.widthBlocks = widthBlocks
        player.color = color
        return player
    }

    override fun readObject(ois: ObjectInputStream): MovingObject {
        super.readObject(ois)
        if (gameState.serializationVersion >= 2) {
            heightBlocks = ois.readInt()
            widthBlocks = ois.readInt()
            color = GameObjectColor.fromInt(ois.readInt())
        }
        return this
    }
    override fun writeObject(oos: ObjectOutputStream): MovingObject {
        super.writeObject(oos)
        oos.writeInt(heightBlocks)
        oos.writeInt(widthBlocks)
        oos.writeInt(color.ord)
        return this
    }

    override fun toString(): String {
        return "Player(x=$x, y=$y, xspeed=$xspeed, yspeed=$yspeed, width=$widthBlocks, height=$heightBlocks)"
    }
}
