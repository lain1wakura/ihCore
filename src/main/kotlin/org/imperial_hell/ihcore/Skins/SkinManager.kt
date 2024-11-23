package org.imperial_hell.ihcore.Skins
import org.imperial_hell.ihcore.Files.IhConfig
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

object SkinManager {

    enum class Model(val value: String) {
        SLIM("slim"),
        CLASSIC("classic");

        companion object {
            fun fromString(value: String): Model? {
                return Model.entries.find { it.value == value }
            }
        }

    }

    // Метод для загрузки скина и сохранения его в указанной директории
    fun downloadSkin(url: String, playerName: String, model: Model) {
        try {
            // Создаем путь к директории для скинов
            val skinDirectoryPath = Paths.get("${IhConfig.absolutePath}/skins/$playerName")

            // Проверяем, существует ли директория, если нет - создаем
            if (!Files.exists(skinDirectoryPath)) {
                Files.createDirectories(skinDirectoryPath)
            }

            // Устанавливаем путь для сохранения файла с учетом модели
            val skinFilePath = skinDirectoryPath.resolve("skin_${model.value}.png")

            // Загружаем изображение с URL
            val uri = URI(url)  // Преобразуем строку URL в URI
            val connection = uri.toURL().openConnection()
            connection.connect()

            // Скачиваем изображение и сохраняем в файл
            connection.getInputStream().use { input ->
                FileOutputStream(skinFilePath.toFile()).use { output ->
                    input.copyTo(output)  // Копируем данные из потока в файл
                }
            }

            println("Скин успешно загружен и сохранен в: $skinFilePath")
        } catch (e: Exception) {
            println("Произошла ошибка при загрузке скина: ${e.message}")
        }
    }

}