package cz.woitee.utils.pools

import cz.woitee.game.undoing.IUndo
import java.util.*

/**
 * Created by woitee on 04/06/2017.
 */
object DefaultUndoListPool: SimplePool<ArrayList<IUndo>>(DefaultUndoListFactory()) {
    class DefaultUndoListFactory: SimpleFactory<ArrayList<IUndo>>() {
        override fun create(): ArrayList<IUndo> {
            return ArrayList()
        }
        override fun passivateObject(obj: ArrayList<IUndo>) {
            obj.clear()
        }
    }
}