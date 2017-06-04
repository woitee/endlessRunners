package Game.GameObjects

import Game.BlockHeight
import Game.BlockWidth
import Game.GameState
import Geom.Vector2Double
import Geom.Vector2Int
import Utils.Pools.DefaultVector2DoublePool
import Utils.arrayList
import java.util.*

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameObject(var x: Double = 0.0, var y: Double = 0.0) {
    abstract val gameObjectClass: GameObjectClass

    open var isUpdated: Boolean = false
    open val isSolid: Boolean = false
    open val widthBlocks = 1
    open val heightBlocks: Int = 1

    var location: Vector2Double
        get() = Vector2Double(x, y)
        set(value) {x = value.x; y = value.y}

    lateinit var gameState: GameState

    val widthPx: Int
        get() = widthBlocks * BlockWidth
    val heightPx: Int
        get() = heightBlocks * BlockHeight

    open fun update(time: Double) {}

    var _corners:ArrayList<Vector2Double>? = null
    val corners: ArrayList<Vector2Double>
        get() {
            if (_corners == null) {
                _corners = arrayList(4, { -> Vector2Double() })
            }

            _corners!![0].x = x
            _corners!![0].y = y

            _corners!![1].x = x + widthBlocks * BlockWidth
            _corners!![1].y = y

            _corners!![2].x = x
            _corners!![2].y = y + heightBlocks * BlockHeight

            _corners!![3].x = x + widthBlocks * BlockWidth
            _corners!![3].y = y + heightBlocks * BlockHeight

            return _corners!!
        }
}

