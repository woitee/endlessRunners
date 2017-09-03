package utils

import java.io.ObjectInputStream
import java.io.ObjectOutputStream

interface MySerializable {
    fun writeObject(oos: ObjectOutputStream)
    fun readObject(ois: ObjectInputStream)
}