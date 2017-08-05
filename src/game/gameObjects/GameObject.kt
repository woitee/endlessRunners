package game.gameObjects

import game.BlockHeight
import game.BlockWidth
import game.GameState
import geom.Vector2Double
import utils.arrayList
import java.util.*

/**
 * Created by woitee on 13/01/2017.
 */

abstract class GameObject(var x: Double = 0.0, var y: Double = 0.0) {
    abstract val gameObjectClass: GameObjectClass

    open var isUpdated: Boolean = false
    open val isSolid: Boolean = false
    open val defaultWidthBlocks: Int = 1
    open val defaultHeightBlocks: Int = 1
    open var widthBlocks: Int = 1
    open var heightBlocks: Int = 1

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
                _corners = arrayList(4, { Vector2Double() })
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

    var _collPoints:ArrayList<Vector2Double>? = null
    val collPoints: ArrayList<Vector2Double>
        get() {
            val desiredSize = (widthBlocks + 1) * (heightBlocks + 1)
            if (_collPoints == null || _collPoints!!.count() != desiredSize) {
                _collPoints = arrayList(desiredSize, { Vector2Double() })
            }

            var i = 0
            for (col in 0 .. widthBlocks) {
                for (row in 0 .. heightBlocks) {
                    _collPoints!![i].x = x + col * BlockWidth
                    _collPoints!![i].y = y + row * BlockHeight
                    // Objects are slightly lower so they fit under stuff
                    if (row == heightBlocks)
                        _collPoints!![i].y -= 1
                    ++i
                }
            }

            return _collPoints!!
        }

}

