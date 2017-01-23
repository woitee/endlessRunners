package Game.GameObjects

import Game.BlockHeight
import Game.BlockWidth
import Game.GameObjects.GameObject
import Geom.Direction4
import Geom.Vector2Double
import Geom.direction4

/**
 * Created by woitee on 15/01/2017.
 */

abstract class MovingObject(x:Double = 0.0, y:Double = 0.0): GameObject(x, y) {
    override var isUpdated = true
    // speeds are entered in pixels per second
    var xspeed = 0.0
    var yspeed = 0.0

    var velocity: Vector2Double
        get() = Vector2Double(xspeed, yspeed)
        set(value) {xspeed = value.x; yspeed = value.y}

    override fun update(time: Long) {
        gameState.game.collHandler.handleCollisions(this)

        this.x += xspeed * time
        this.y += yspeed * time
    }
}