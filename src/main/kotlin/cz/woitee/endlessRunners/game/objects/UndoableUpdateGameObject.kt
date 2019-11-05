package cz.woitee.endlessRunners.game.objects

import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * Created by woitee on 09/04/2017.
 */
abstract class UndoableUpdateGameObject(x: Double, y: Double) : GameObject(x, y) {
    abstract fun undoableUpdate(time: Double): IUndo
}
