package org.imperial_hell.qbrp.Resources.ContentUnits

import org.imperial_hell.qbrp.Resources.ISavable
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

abstract class ContentUnit(
    path: Path,
    name: String,
    extension: String = "txt", // Укажите расширение по умолчанию, например, txt
    val key: UnitKey = UnitKey("unset")
) : ResourceUnit(path.resolve("$name.$extension") as Path), ISavable {

    override fun handle(): ResourceUnit {
        val filePath = this.path.toFile()
        if (!filePath.exists()) {
            try {
                filePath.createNewFile() // Создаёт файл с указанным расширением
            } catch (e: Exception) {
                throw IllegalStateException("Не удалось создать файл: ${filePath.path}", e)
            }
        }
        save()
        structure?.registerContent(this)
        return this
    }
}
