package cz.woitee.endlessRunners.game.objects

import java.io.Serializable

/**
 * A block representing a static object in the game, that usually has custom meaning based on the GameDescription,
 * mainly the collision mapping.
 */
class CustomBlock(val type: Int, override val isSolid: Boolean = true, x: Double = 0.0, y: Double = 0.0) : GameObject(x, y), Serializable {

    override val gameObjectClass = GameObjectClass.fromInt(GameObjectClass.CUSTOM0.ord + type)
    override var color = GameObjectColor.fromInt(GameObjectColor.GREEN.ord + type)

    override val dumpChar = '0' + type

    override fun makeCopy(): GameObject {
        return CustomBlock(type, isSolid, x, y)
    }
}
