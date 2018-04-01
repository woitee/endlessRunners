package cz.woitee.game.objects

import cz.woitee.game.BlockWidth
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by woitee on 13/01/2017.
 */

class Player(x: Double, y: Double): MovingObject(x, y) {
    override val gameObjectClass = GameObjectClass.PLAYER

    override val defaultHeightBlocks = 2
    override var heightBlocks = 2

    override val dumpChar = 'P'

    val defaultColor = GameObjectColor.BLUE
    override var color = GameObjectColor.BLUE

    fun positionOnScreen(): Double {
        return this.x - (gameState.gridX * BlockWidth)
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
}