package Game.GameObjects

/**
 * Created by woitee on 13/01/2017.
 */

class Player(x: Double = 0.0, y:Double = 0.0): MovingObject(x, y) {
    override val heightBlocks = 2
}