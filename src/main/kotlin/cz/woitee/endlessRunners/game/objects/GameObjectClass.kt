package cz.woitee.endlessRunners.game.objects

/**
 * An enumaration class representing the type of the object.
 */

enum class GameObjectClass(val ord: Int) {
    PLAYER(0), SOLIDBLOCK(1),
    CUSTOM0(2), CUSTOM1(3), CUSTOM2(4), CUSTOM3(5),
    INVALID(6);

    companion object {
        private val map = GameObjectClass.values().associateBy(GameObjectClass::ord)
        fun fromInt(ord: Int) = map[ord]!!
    }
}
