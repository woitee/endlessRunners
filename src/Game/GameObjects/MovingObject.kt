package Game.GameObjects

import Game.GameObjects.GameObject

/**
 * Created by woitee on 15/01/2017.
 */

open class MovingObject(x:Double = 0.0, y:Double = 0.0): GameObject(x, y) {
    override var isUpdated = true
    // speeds are entered in pixels per second
    var xspeed = 0.0
    var yspeed = 0.0

    override fun update(time: Long) {
        this.x += xspeed * time
        this.y += yspeed * time
    }
}