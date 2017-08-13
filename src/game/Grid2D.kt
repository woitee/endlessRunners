package game

import geom.Vector2Int
import java.util.*
import utils.arrayList
import utils.shift

/**
 * A helper grid that describes the objects as a 2D Grid.
 *
 * Created by woitee on 13/01/2017.
 */

class Grid2D<T>(val width: Int, val height: Int, val factory: ()->T) {
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

    operator fun set(x: Int, y: Int, obj: T): Grid2D<T> {
        grid[x][y] = obj
        return this
    }

    fun setColumn(x: Int, column: ArrayList<T>): Grid2D<T>  {
        grid[x] = column
        return this
    }

    fun shiftX(amount: Int) {
        grid.shift(amount, { arrayList(height, factory) })
    }

    fun shiftY(amount: Int) {
        for (col in grid) {
            col.shift(amount, factory)
        }
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
            i -> println()
        })
    }
}