package cz.woitee.endlessRunners.utils

import java.io.File

/**
 * Returns a file object, and ensures it has already prepared directory structure.
 */
fun fileWithCreatedPath(filePath: String): File {
    val file = File(filePath)
    file.parentFile.mkdirs()
    return file
}
