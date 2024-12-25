package org.imperial_hell.qbrp.Resources.ContentUnits

import org.imperial_hell.qbrp.Resources.Data.TextureData
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class TextureUnit(
    path: Path,
    key: UnitKey = UnitKey("unset"),
    val name: String,
    val data: TextureData
) : ContentUnit(path, name, "png", key) {

    // Метод для копирования текстуры в новый путь
    override fun save() {
        val sourcePath = Path.of(data.texturePath) // Исходный путь к изображению
        val destinationPath = path // Путь назначения (например, output/texture.png)

        try {
            // Копируем файл
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING)
        } catch (e: Exception) {
            println("Ошибка при копировании файла: ${e.message}")
        }
    }
}
