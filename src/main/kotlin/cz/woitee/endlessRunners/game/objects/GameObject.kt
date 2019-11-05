package cz.woitee.endlessRunners.game.objects

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.BlockWidth
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.geom.Vector2Double
import cz.woitee.endlessRunners.utils.MySerializable
import cz.woitee.endlessRunners.utils.arrayList
import cz.woitee.endlessRunners.utils.resizeTo
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.jvm.Transient

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameObject(var x: Double = 0.0, var y: Double = 0.0) : MySerializable {
    abstract val gameObjectClass: GameObjectClass

    open var isUpdated: Boolean = false
    open val isSolid: Boolean = false
    open val defaultWidthBlocks: Int = 1
    open val defaultHeightBlocks: Int = 1
    open var widthBlocks: Int = 1
    open var heightBlocks: Int = 1

    open val dumpChar = '?'
    open var color = GameObjectColor.UNSPECIFIED

    var location: Vector2Double
        get() = Vector2Double(x, y)
        set(value) { x = value.x; y = value.y }

    @Transient lateinit var gameState: GameState

    val widthPx: Int
        get() = widthBlocks * BlockWidth
    val heightPx: Int
        get() = heightBlocks * BlockHeight

    open fun update(time: Double) {}

    val corners: ArrayList<Vector2Double> = arrayList(4, { Vector2Double() })
        get() {
            field[0].x = x
            field[0].y = y

            field[1].x = x + widthBlocks * BlockWidth
            field[1].y = y

            field[2].x = x
            field[2].y = y + heightBlocks * BlockHeight

            field[3].x = x + widthBlocks * BlockWidth
            field[3].y = y + heightBlocks * BlockHeight

            return field
        }

    val collPointsNumber
        get() = (widthBlocks + 1) * (heightBlocks + 1)
    val collPoints: ArrayList<Vector2Double> = arrayList(collPointsNumber, { Vector2Double() })
        get() {
            if (field.count() != collPointsNumber) {
                field.resizeTo(collPointsNumber, { Vector2Double() })
            }

            var i = 0
            for (col in 0 .. widthBlocks) {
                for (row in 0 .. heightBlocks) {
                    field[i].x = x + col * BlockWidth
                    field[i].y = y + row * BlockHeight
                    // Objects are slightly lower so they fit under stuff
                    if (row == heightBlocks)
                        field[i].y -= 1
                    ++i
                }
            }

            return field
        }

    abstract fun makeCopy(): GameObject

    override fun readObject(ois: ObjectInputStream): GameObject {
        x = ois.readDouble()
        y = ois.readDouble()
        heightBlocks = ois.readInt()
        widthBlocks = ois.readInt()
        return this
    }
    override fun writeObject(oos: ObjectOutputStream): GameObject {
        oos.writeDouble(x)
        oos.writeDouble(y)
        oos.writeInt(heightBlocks)
        oos.writeInt(widthBlocks)
        return this
    }
}
