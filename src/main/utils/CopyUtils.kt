package utils

import java.io.*

object CopyUtils {
    inline fun <reified T: MySerializable> copyBySerialization(obj: T): T {
        var oos: ObjectOutputStream? = null
        var ois: ObjectInputStream? = null
        try {
            val bos = ByteArrayOutputStream()
            oos = ObjectOutputStream(bos)
            // serialize and pass the object

            obj.writeObject(oos)
            oos.flush()
            val bin = ByteArrayInputStream(bos.toByteArray())
            ois = ObjectInputStream(bin)
            // return the new object
            val copy = getInstanceOfObject<T>()
            copy.readObject(ois)
            return copy
        } finally {
            oos?.close()
            ois?.close()
        }
    }

    inline fun <reified T: MySerializable> getInstanceOfObject(): T {
        for (constructor in T::class.constructors) {
            val count = constructor.parameters.count { !it.isOptional }
            if (count == 0) {
                return constructor.call()
            }
        }
        throw Exception("Unable to copy class without zero-parameter constuctors.")
    }
}