package cz.woitee.endlessRunners.game

import cz.woitee.endlessRunners.geom.Vector2Int
import cz.woitee.endlessRunners.utils.arrayList
import cz.woitee.endlessRunners.utils.resizeTo
import cz.woitee.endlessRunners.utils.shift
import java.util.*

/**
 * A helper grid that describes the objects as a 2D Grid.
 */

class Grid2D<T>(width: Int, height: Int, val factory: () -> T) {
    var width: Int = width
        protected set
    var height: Int = height
        protected set
    var grid = arrayList(width, { arrayList(height, factory) })

    operator fun get(x: Int, y: Int): T {
        try {
            return grid[x][y]
        } catch (e: IndexOutOfBoundsException) {
            throw IndexOutOfBoundsException("Invalid index x:$x y:$y (size ($width, $height))")
        }
    }

    operator fun get(p: Vector2Int): T {
        return this[p.x, p.y]
    }

    fun safeGet(x: Int, y: Int): T? {
        return if (contains(x, y)) this[x, y]
        else null
    }

    fun safeGet(p: Vector2Int): T? {
        return safeGet(p.x, p.y)
    }

    fun safeSet(x: Int, y: Int, obj: T): Grid2D<T> {
        if (contains(x, y)) set(x, y, obj)
        return this
    }
    fun safeSet(p: Vector2Int, obj: T): Grid2D<T> {
        return safeSet(p.x, p.y, obj)
    }

    operator fun set(x: Int, y: Int, obj: T): Grid2D<T> {
        grid[x][y] = obj
        return this
    }

    operator fun set(p: Vector2Int, obj: T): Grid2D<T> {
        return set(p.x, p.y, obj)
    }

    fun getColumn(x: Int): ArrayList<T> {
        return grid[x]
    }

    fun setColumn(x: Int, column: ArrayList<T>): Grid2D<T> {
        grid[x] = column
        return this
    }

    fun shiftX(amount: Int) {
        grid.shift(amount) { arrayList(height, factory) }
    }

    fun shiftY(amount: Int) {
        for (col in grid) {
            col.shift(amount, factory)
        }
    }

    fun resizeWidth(targetWidth: Int) {
        grid.resizeTo(targetWidth, { arrayList(height, factory) })
        this.width = targetWidth
    }

    fun resizeHeight(targetHeight: Int) {
        for (column in grid) {
            column.resizeTo(targetHeight,
                    factory)
        }
        this.height = targetHeight
    }

    fun clear() {
        for (col in grid) {
            for (i in 0 until height)
                col[i] = factory()
        }
    }

    fun contains(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height
    }
    fun contains(point: Vector2Int): Boolean {
        return contains(point.x, point.y)
    }

    fun forEach(action: (T) -> Unit, afterRow: (Int) -> Unit = {}) {
        for (y in height - 1 downTo 0) {
            for (x in 0 .. width - 1) {
                action(grid[x][y])
            }
            afterRow(y)
        }
    }

    fun debugPrint() {
        forEach({
            gameObject -> print(if (gameObject == null) " " else "#")
        }, {
            _ -> println()
        })
    }
}
