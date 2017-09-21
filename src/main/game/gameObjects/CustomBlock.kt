package game.gameObjects

class CustomBlock(val type: Int, x:Double = 0.0, y:Double = 0.0): GameObject(x, y) {
    override val gameObjectClass = GameObjectClass.fromInt(GameObjectClass.CUSTOM0.ord + type)
    override val dumpChar = '0' + type
    override val isSolid = true

    override fun makeCopy(): GameObject {
        return CustomBlock(type, x, y)
    }
}