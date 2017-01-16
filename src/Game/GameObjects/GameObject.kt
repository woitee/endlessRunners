package Game.GameObjects

import Game.BlockHeight
import Game.BlockWidth

/**
 * Created by woitee on 13/01/2017.
 */

open class GameObject(var x: Double = 0.0, var y: Double = 0.0) {
    open var isUpdated: Boolean = false
    open val widthBlocks = 1
    open val heightBlocks: Int = 1

    val widthPx: Int
        get() = widthBlocks * BlockWidth
    val heightPx: Int
        get() = heightBlocks * BlockHeight

    open fun update(time: Long) {}
}

