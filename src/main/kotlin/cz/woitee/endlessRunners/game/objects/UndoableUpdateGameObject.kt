package cz.woitee.endlessRunners.game.objects

import cz.woitee.endlessRunners.game.undoing.IUndo

/**
 * Object, which behavior can be undone.
 */
abstract class UndoableUpdateGameObject(x: Double, y: Double) : GameObject(x, y) {
    abstract fun undoableUpdate(time: Double): IUndo
}
