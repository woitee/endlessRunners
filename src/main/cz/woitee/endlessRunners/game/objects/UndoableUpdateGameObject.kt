package cz.woitee.game.objects

import cz.woitee.game.undoing.IUndo

/**
 * Created by woitee on 09/04/2017.
 */
abstract class UndoableUpdateGameObject(x: Double, y: Double): GameObject(x, y) {
    abstract fun undoableUpdate(time: Double): IUndo
}