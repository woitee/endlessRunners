package cz.woitee.endlessRunners.utils

import java.io.*

/**
 * Utils for copying by serialization.
 */
object CopyUtils {
    inline fun <reified T : MySerializable> copyBySerialization(sourceObj: T, targetObj: T): T {
        var oos: ObjectOutputStream? = null
        var ois: ObjectInputStream? = null
        try {
            val bos = ByteArrayOutputStream()
            oos = ObjectOutputStream(bos)
            // serialize and pass the object

            sourceObj.writeObject(oos)
            oos.flush()
            val bin = ByteArrayInputStream(bos.toByteArray())
            ois = ObjectInputStream(bin)
            // return the new object
            targetObj.readObject(ois)
            return targetObj
        } finally {
            oos?.close()
            ois?.close()
        }
    }

    inline fun <reified T : Serializable> copyByJavaSerialization(sourceObj: T): T {
        var oos: ObjectOutputStream? = null
        var ois: ObjectInputStream? = null
        try {
            val bos = ByteArrayOutputStream()
            oos = ObjectOutputStream(bos)
            // serialize and pass the object

            oos.writeObject(sourceObj)
            oos.flush()
            val bin = ByteArrayInputStream(bos.toByteArray())
            ois = ObjectInputStream(bin)
            // return the new object
            return ois.readObject() as T
        } finally {
            oos?.close()
            ois?.close()
        }
    }
}
