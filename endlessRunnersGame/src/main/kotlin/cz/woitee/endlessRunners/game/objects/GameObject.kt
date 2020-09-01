package cz.woitee.endlessRunners.game.objects

import cz.woitee.endlessRunners.game.BlockHeight
import cz.woitee.endlessRunners.game.BlockWidth
import cz.woitee.endlessRunners.game.GameState
import cz.woitee.endlessRunners.geom.Vector2Double
import cz.woitee.endlessRunners.utils.MySerializable
import cz.woitee.endlessRunners.utils.arrayList
import cz.woitee.endlessRunners.utils.resizeTo
import nl.pvdberg.hashkode.compareFields
import java.io.*
import java.util.*
import kotlin.jvm.Transient

/**
 * An object in a game. Provides access to its attributes and operations with its location.
 */

abstract class GameObject(var x: Double = 0.0, var y: Double = 0.0) : MySerializable, Serializable {
    abstract val gameObjectClass: GameObjectClass

    /** Whether the object should be notified in the update loop */
    open var isUpdated: Boolean = false
    /** Whether the object can be passed through by a player */
    open val isSolid: Boolean = false
    /** Basic width of the object */
    open val defaultWidthBlocks: Int = 1
    /** Basic height of the object */
    open val defaultHeightBlocks: Int = 1
    /** Current width of the object */
    open var widthBlocks: Int = 1
    /** Current height of the object */
    open var heightBlocks: Int = 1

    /** The char to show in a textdump of a GameState */
    open val dumpChar = '?'
    /** Color of the object */
    open var color = GameObjectColor.UNSPECIFIED

    val isCustomBlock
        get() = gameObjectClass.ord >= GameObjectClass.CUSTOM0.ord && gameObjectClass.ord <= GameObjectClass.CUSTOM3.ord

    var location: Vector2Double
        get() = Vector2Double(x, y)
        set(value) { x = value.x; y = value.y }

    @Transient lateinit var gameState: GameState

    val widthPx: Int
        get() = widthBlocks * BlockWidth
    val heightPx: Int
        get() = heightBlocks * BlockHeight

    /**
     * Updates the object, should be called every frame if isUpdate is true.
     */
    open fun update(time: Double) {}

    /**
     * Locations of the 4 corners of this object.
     */
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

    /** Number of the collision points on this object */
    val collPointsNumber
        get() = (widthBlocks + 1) * (heightBlocks + 1)
    /**
     * Locations of all the collision points of this object.
     * Small objects use their corners by default.
     */
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

    override fun equals(other: Any?) = compareFields(other) {
        equal = one.gameObjectClass == two.gameObjectClass &&
                one.x == two.x &&
                one.y == two.y &&
                one.isSolid == two.isSolid
    }

    override fun toString(): String {
        return "GameObject($dumpChar)"
    }

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
