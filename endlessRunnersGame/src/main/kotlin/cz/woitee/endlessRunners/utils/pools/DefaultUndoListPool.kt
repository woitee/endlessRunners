package cz.woitee.endlessRunners.utils.pools

import cz.woitee.endlessRunners.game.undoing.IUndo
import java.util.*

/**
 * A minimalistic pool for undoList objects. Used to alleviate GC.
 */
object DefaultUndoListPool : SimplePool<ArrayList<IUndo>>(DefaultUndoListFactory()) {
    class DefaultUndoListFactory : SimpleFactory<ArrayList<IUndo>>() {
        override fun create(): ArrayList<IUndo> {
            return ArrayList()
        }
        override fun passivateObject(obj: ArrayList<IUndo>) {
            obj.clear()
        }
    }
}
