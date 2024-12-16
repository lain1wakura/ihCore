package org.imperial_hell.qbrp.client.Sync

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Files.IhConfig
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.pathString

object ResourceLoader {

    private val client = OkHttpClient()

    fun unzip(zipFile: File, targetDirectory: File) {
        try {
            if (!targetDirectory.exists()) targetDirectory.mkdirs()

            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry

                while (entry != null) {
                    val newFile = File(targetDirectory, entry.name)

                    if (entry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        newFile.parentFile.mkdirs()
                        FileOutputStream(newFile).use { fos ->
                            zis.copyTo(fos)
                        }
                    }

                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }

            IhLogger.log("Архив успешно разархивирован в: ${targetDirectory.absolutePath}", IhLogger.MessageType.SUCCESS)
        } catch (e: Exception) {
            IhLogger.log("Ошибка при разархивировании: ${e.message}", IhLogger.MessageType.ERROR)
        }
    }

    fun downloadFile(url: String, targetDirectory: File, fileName: String) {
        try {
            val request = Request.Builder().url(url).build()
            val response: Response = client.newCall(request).execute()

            if (response.isSuccessful) {
                if (!targetDirectory.exists()) targetDirectory.mkdirs()
                val file = File(targetDirectory, fileName)
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(response.body!!.bytes())
                }

                IhLogger.log("Файл успешно загружен: <<${file.absolutePath}>>", IhLogger.MessageType.SUCCESS, debugMode = true)
            } else {
                IhLogger.log("Ошибка загрузки файла: ${response.code}", IhLogger.MessageType.ERROR)
            }
        } catch (e: Exception) {
            IhLogger.log("Ошибка загрузки файла: ${e.message}", IhLogger.MessageType.ERROR)
        }
    }

    fun extract(zipFile: File, extractDirectory: File) {
        try {
            // Проверяем, что архив существует
            if (!zipFile.exists()) {
                IhLogger.log("Ошибка: ZIP-файл не найден: ${zipFile.absolutePath}", IhLogger.MessageType.ERROR)
                return
            }

            IhLogger.log("Начинаем разархивирование файла: ${zipFile.absolutePath}", IhLogger.MessageType.INFO)

            // Создаём директорию для извлечения, если её нет
            if (!extractDirectory.exists()) {
                extractDirectory.mkdirs()
                IhLogger.log("Создана директория для извлечения: ${extractDirectory.absolutePath}", IhLogger.MessageType.INFO)
            }

            // Разархивирование
            unzip(zipFile, extractDirectory)
            IhLogger.log("Архив успешно разархивирован в: ${extractDirectory.absolutePath}", IhLogger.MessageType.SUCCESS)

        } catch (e: Exception) {
            IhLogger.log("Ошибка при разархивировании файла: ${e.message}", IhLogger.MessageType.ERROR)
        }
    }

    fun downloadResources() {
        val url = IhConfig.DOWNLOAD_URL
        val targetDirectory = File(IhConfig.DOWNLOAD_DIR)
        val zipFileName = "resources.zip"
        val zipFile = File(targetDirectory, zipFileName)

        if (File(IhConfig.PACK_DIR.pathString, "assets/qbrp/models").exists()) {
            Files.walk(File(IhConfig.PACK_DIR.pathString, "assets/qbrp/models").toPath())
                .sorted(Comparator.reverseOrder())
                .map { it.toFile() }
                .filter { it.name.startsWith("custom_item_") } // Фильтруем файлы, которые начинаются на custom_item_
                .forEach { it.delete() }
        }

        try {
            // Скачиваем архив
            downloadFile(url, targetDirectory, zipFileName)
            IhLogger.log("Файл успешно скачан: ${zipFile.absolutePath}", IhLogger.MessageType.INFO)

            // Перемещаем или обрабатываем скачанный файл перед извлечением (если нужно)
            val extractDirectory = IhConfig.PACKS_DIR // Используем ту же директорию для разархивирования
            extract(zipFile, extractDirectory.toFile())

        } catch (e: Exception) {
            IhLogger.log("Ошибка при загрузке или разархивировании ресурсов: ${e.message}", IhLogger.MessageType.ERROR)
        }
    }

    fun writeFile(filePath: String, content: String) {
        try {
            val file = File(filePath)
            file.parentFile?.mkdirs()
            FileOutputStream(file).use { fos ->
                fos.write(content.toByteArray())
            }
            IhLogger.log("JSON файл записан: ${file.path}", IhLogger.MessageType.INFO)
        } catch (e: Exception) {
            IhLogger.log("Ошибка записи JSON файла: ${filePath} | ${e.message}", IhLogger.MessageType.ERROR)
        }
    }
}
