package cz.woitee.utils

import cz.woitee.game.GameState
import java.io.File
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun GameState.dumpToFile(logPrefix: String = "GameStateDump", logPath: String = "out/states") {
    val logFileName = "${logPath}/${logPrefix}_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Date()) + ".dmp"
    val file = File(logFileName)
    val oos = ObjectOutputStream(file.outputStream())
    this.writeObject(oos)
    oos.flush()
    oos.close()
}