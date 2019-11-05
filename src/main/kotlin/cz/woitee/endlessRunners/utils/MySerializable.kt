package cz.woitee.endlessRunners.utils

import java.io.ObjectInputStream
import java.io.ObjectOutputStream

interface MySerializable {
    fun writeObject(oos: ObjectOutputStream): MySerializable
    fun readObject(ois: ObjectInputStream): MySerializable
}
