package org.imperial_hell.qbrp.Resources

import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Resources.Structure.PackStructure
import java.io.File

class ResourcePack(val structure: PackStructure) {
    val content = PackContent(structure)
    private var baked = false

    fun isBaked(): Boolean { return baked }

    // Метод finish, принимающий функцию, которая определяет значение переменной baked
    fun finish(zipDirectory: File, checkBakingStatus: (PackStructure) -> Boolean) {
        baked = checkBakingStatus(structure) // Устанавливаем значение baked, вызвав переданную функцию
        try {
            structure.tree.zip(zipDirectory)
        } catch (e: Exception) {
            IhLogger.log("Возникла ошибка при архивации пакета ресурсов: ${e.stackTrace}")
            baked = false
        }
    }
}
