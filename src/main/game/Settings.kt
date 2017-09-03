package game

/**
 * Created by woitee on 09/01/2017.
 */


val GameWidth = 700
val GameHeight = 400
val BlockWidth = 24
val BlockHeight = 24
val WidthBlocks = Math.ceil(GameWidth.toDouble() / BlockWidth).toInt() + 1
val HeightBlocks = Math.ceil(GameHeight.toDouble() / BlockHeight).toInt() + 1

val GameFPS = 75

val PlayerScreenX = 90.0