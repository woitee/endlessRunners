package cz.woitee.endlessRunners.game.objects

/**
 * The most default - solid object in the game, used as a basic platform.
 */

class SolidBlock(x: Double = 0.0, y: Double = 0.0) : GameObject(x, y) {
    // parameterless constructor for serialization purposes
    constructor() : this(0.0, 0.0)

    override val gameObjectClass = GameObjectClass.SOLIDBLOCK
    override val isSolid: Boolean = true

    override val dumpChar = '#'

    override fun makeCopy(): GameObject {
        return SolidBlock(this.x, this.y)
    }
}
