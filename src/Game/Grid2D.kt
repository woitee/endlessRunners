package Game

import java.util.*
import Utils.arrayList
import Utils.shift

/**
 * A helper grid that describes the objects as the
 *
 * Created by woitee on 13/01/2017.
 */

class Grid2D<T>(val width: Int, val height: Int, val factory: ()->T) {
    var grid = arrayList(width, { arrayList(height, factory) })

    operator fun get(x: Int, y: Int): T {
        return grid[x][y]
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