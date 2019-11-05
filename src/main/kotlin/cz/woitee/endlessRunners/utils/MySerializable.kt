package cz.woitee.endlessRunners.utils

import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * A custom interface for serialization, due to finding problems with both Kotlin and Java serialization (reported
 * to Wiki tracker).
 */
interface MySerializable {
    fun writeObject(oos: ObjectOutputStream): MySerializable
    fun readObject(ois: ObjectInputStream): MySerializable
}
