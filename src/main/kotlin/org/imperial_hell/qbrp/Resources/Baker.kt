package org.imperial_hell.qbrp.Resources

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.nio.file.Path
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Files.IhConfig
import org.imperial_hell.qbrp.Resources.Data.ModelData
import java.io.File
import java.nio.file.Paths

object Baker {

    // Сканирование моделей
    fun scanModels(directory: File): List<File> =
        //scanFiles(directory, "json").mapNotNull { file -> parseJson(file)?.let { ModelData(it) } }
        scanFiles(directory, "json")

    fun scanTextures(directory: File): List<File> =
        scanFiles(directory, "png")

    // Получение текстуры относительно базовой директории
    fun getTexture(relativePath: String): File? {
        val file = File(IhConfig.SERVER_RESOURCES_PATH.toFile(), relativePath)
        return validateFile(file)?.takeIf { it.extension.equals("png", ignoreCase = true) } ?: run {
            IhLogger.log("Файл не найден или не является PNG: ${file.absolutePath}", IhLogger.MessageType.ERROR)
            null
        }
    }

    // Универсальный метод для поиска файлов с указанным расширением
    private fun scanFiles(directory: File, extension: String): List<File> =
        directory.takeIf { it.exists() && it.isDirectory }?.walkTopDown()
            ?.filter { it.isFile && it.extension.equals(extension, ignoreCase = true) }
            ?.toList()
            ?: run {
                IhLogger.log("Директория не найдена или не является папкой: ${directory.absolutePath}", IhLogger.MessageType.ERROR)
                emptyList()
            }

    // Парсинг JSON
    fun parseJson(file: File): JsonObject? =
        validateFile(file)?.let {
            runCatching {
                JsonParser.parseString(it.readText()).asJsonObject
            }.getOrElse { ex ->
                IhLogger.log("Ошибка при парсинге файла ${file.absolutePath}: ${ex.message}", IhLogger.MessageType.ERROR)
                null
            }
        }

    // Проверка файла
    private fun validateFile(file: File): File? =
        file.takeIf { it.exists() && it.isFile } ?: run {
            IhLogger.log("Файл не найден или не является файлом: ${file.absolutePath}", IhLogger.MessageType.ERROR)
            null
        }
}
