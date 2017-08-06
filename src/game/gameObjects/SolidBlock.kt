package game.gameObjects

/**
 * Created by woitee on 15/01/2017.
 */

class SolidBlock(x: Double = 0.0, y: Double = 0.0): GameObject(x, y) {
    override val gameObjectClass = GameObjectClass.SOLIDBLOCK
    override val isSolid: Boolean = true
}