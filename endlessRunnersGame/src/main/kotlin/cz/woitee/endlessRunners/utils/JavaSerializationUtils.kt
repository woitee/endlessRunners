package cz.woitee.endlessRunners.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * Utils to provide Java default serialization to strings that will actually work.
 */
object JavaSerializationUtils {
    fun serializeToString(obj: Serializable): String {
        return serialize(obj).joinToString(":")
    }

    fun <T> unserializeFromString(str: String): T? {
        @Suppress("UNCHECKED_CAST")
        return unserialize(str.split(":").map { s -> s.toByte() }.toByteArray()) as T?
    }

    fun serialize(sourceObj: Serializable): ByteArray {
        var oos: ObjectOutputStream? = null
        try {
            val bos = ByteArrayOutputStream()
            oos = ObjectOutputStream(bos)
            // serialize and pass the object

            oos.writeObject(sourceObj)
            oos.flush()
            return bos.toByteArray()
        } finally {
            oos?.close()
        }
    }

    fun unserialize(bytes: ByteArray): Any {
        var ois: ObjectInputStream? = null
        try {
            val bin = ByteArrayInputStream(bytes)
            ois = ObjectInputStream(bin)
            // return the new object
            val res = ois.readObject()
            return res
        } finally {
            ois?.close()
        }
    }
}
