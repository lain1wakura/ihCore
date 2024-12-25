package org.imperial_hell.qbrp.Resources.PathFormat

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.nameWithoutExtension

object PathFormat {

    fun getRelative(directoryName: String, path: Path): Path {
        val startIndex = path.iterator().asSequence().indexOfFirst { it.toString() == directoryName }
        return if (startIndex != -1) {
            path.subpath(startIndex + 1, path.nameCount) // Убираем родительские директории и саму директорию
        } else {
            throw IllegalArgumentException("Directory '$directoryName' not found in path: $path")
        }
    }

    // Перегруженная версия метода для строкового пути
    fun getRelative(directoryName: String, pathString: String): String {
        val path = Paths.get(pathString)
        return getRelative(directoryName, path).toString()
    }
}

// Функции-расширения для Path
fun String.getRelative(directoryName: String): String {
    return PathFormat.getRelative(directoryName, this).toString()
}

fun String.toJsonFormat(): String {
    return this.replace("\\", "/")
}

fun Path.getModelType(): String {
    return this.nameWithoutExtension.split('_').last()
}
