package cz.woitee.endlessRunners.game.collisions

import cz.woitee.endlessRunners.game.objects.GameObject
import cz.woitee.endlessRunners.geom.Direction4
import cz.woitee.endlessRunners.geom.Distance2D

data class Collision(
        val other: GameObject,
        val locationX: Double,
        val locationY: Double,
        val myLocationX: Double,
        val myLocationY: Double,
        val direction: Direction4) {

    constructor (other: GameObject, locationX: Double, locationY: Double, myLocationX: Double, myLocationY: Double):
            this (other, locationX, locationY, myLocationX, myLocationY, CollisionUtils.gameObjectLocToDir(other, locationX, locationY))


    val distance: Double
        get() = Distance2D.distance(myLocationX, myLocationY, locationX, locationY)
}