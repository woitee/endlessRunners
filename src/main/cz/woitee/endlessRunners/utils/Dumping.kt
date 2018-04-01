package cz.woitee.utils

import cz.woitee.game.GameState
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun GameState.dumpToFile(logPrefix: String = "GameStateDump", logPath: String = "out/states") {
    val logFileName = "${logPath}/${logPrefix}_" + SimpleDateFormat("yyyy_MM_dd-HH_mm_ss_SSS").format(Date()) + ".dmp"
    val file = File(logFileName)
    val oos = ObjectOutputStream(file.outputStream())
    this.writeObject(oos)
    oos.flush()
    oos.close()
}

fun GameState.readFromFile(filePath: String) {
    val ois2 = ObjectInputStream(File(filePath).inputStream())
    this.readObject(ois2)
    ois2.close()
}