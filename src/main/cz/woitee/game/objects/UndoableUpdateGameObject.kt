package cz.woitee.game.objects

import cz.woitee.game.undoing.IUndo

/**
 * Created by woitee on 09/04/2017.
 */
abstract class UndoableUpdateGameObject(x:Double = 0.0, y:Double = 0.0): GameObject(x, y) {
    abstract fun undoableUpdate(time: Double): IUndo
}