package cz.woitee.endlessRunners.utils

import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * A custom interface for serialization, due to finding problems with both Kotlin and Java serialization (reported
 * to Wiki tracker).
 */
interface MySerializable {
    fun writeObject(oos: ObjectOutputStream): MySerializable
    fun readObject(ois: ObjectInputStream): MySerializable

    fun saveToFile(filename: String) {
        val file = File(filename)
        file.parentFile.mkdirs()

        file.outputStream().use { fos ->
            ObjectOutputStream(fos).use {
                writeObject(it)
            }
        }
    }
    fun loadFromFile(filename: String) = File(filename).inputStream().use { fis ->
        ObjectInputStream(fis).use {
            readObject(it)
        }
    }
}
