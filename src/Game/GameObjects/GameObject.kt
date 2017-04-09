package Game.GameObjects

import Game.BlockHeight
import Game.BlockWidth
import Game.GameState
import Geom.Vector2Double
import Geom.Vector2Int

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

    val corners: Array<Vector2Double>
        get() = arrayOf(
            Vector2Double(x, y),
            Vector2Double(x + widthBlocks * BlockWidth, y),
            Vector2Double(x, y + heightBlocks * BlockHeight),
            Vector2Double(x + widthBlocks * BlockWidth, y + heightBlocks * BlockHeight)
        )
}

