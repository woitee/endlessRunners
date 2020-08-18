package cz.woitee.endlessRunners.utils

import java.io.*

/**
 * A custom interface for serialization, due to finding problems with both Kotlin and Java serialization (reported
 * to Wiki tracker).
 */
interface MySerializable {
    fun writeObject(oos: ObjectOutputStream): MySerializable
    fun readObject(ois: ObjectInputStream): MySerializable

    fun toByteArray(): ByteArray {
        val bos = ByteArrayOutputStream()
        ObjectOutputStream(bos).use {
            writeObject(it)
        }
        return bos.toByteArray()
    }
    fun fromByteArray(byteArray: ByteArray) {
        val bis = ByteArrayInputStream(byteArray)
        return ObjectInputStream(bis).use {
            readObject(it)
        }
    }

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
