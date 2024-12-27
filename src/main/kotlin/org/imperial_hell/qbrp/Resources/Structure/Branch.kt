package org.imperial_hell.qbrp.Resources.Structure

import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.ihSystems.IhLogger.MessageType
import org.imperial_hell.qbrp.Resources.Baker
import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.ContentUnits.ResourceUnit
import org.imperial_hell.qbrp.Resources.Data.RawData
import org.imperial_hell.qbrp.Resources.UnitKey
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.name
import kotlin.reflect.full.primaryConstructor

open class Branch(path: Path,
                  val key: UnitKey = Branches.UNSET.key,
                  val name: String = path.name): ResourceUnit(path) {

    val children: MutableList<ResourceUnit> = mutableListOf()

    override fun handle(): ResourceUnit {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path) // Создаёт директории, если их нет
            }
        } catch (e: Exception) {
            throw IllegalStateException("Не удалось создать директорию: $path", e)
        }

        structure?.registerBranch(this)
        return this
    }

    fun addContainer(): UContainer {
        return add(UContainer(path)) as UContainer
    }

    fun addBranch(name: String, key: UnitKey = Branches.UNSET.key): Branch {
        return add(Branch(path.resolve(name), name = name, key = key)) as Branch
    }

    fun addUnit(data: RawData, key: UnitKey, name: String, extension: String): ContentUnit {
        val clazz = data.unit.kotlin
        val constructor = clazz.primaryConstructor ?: throw IllegalArgumentException("Конструктор не найден")
        return add(constructor.call(path, name, extension, data, key) as ResourceUnit) as ContentUnit
    }

    fun add(unit: ResourceUnit): ResourceUnit {
        unit.structure = structure
        return unit.handle().also {
            IhLogger.log("<<[+]>> ${unit.path}")
            children.add(it)
        }
    }

    fun zip(outputZip: File, sourceFolder: File = path.toFile()) {
        // Проверяем существование и создаем выходной файл, если нужно
        outputZip.apply {
            parentFile?.mkdirs()
            if (!exists()) createNewFile()
            require(canWrite()) { "Нет доступа для записи: $absolutePath" }
        }

        require(sourceFolder.exists() && sourceFolder.isDirectory) {
            "Исходная папка должна существовать и быть директорией: ${sourceFolder.absolutePath}"
        }

        // Создаем ZIP-архив
        ZipOutputStream(outputZip.outputStream()).use { zipOut ->
            sourceFolder.walkTopDown().forEach { file ->
                val relativePath = file.relativeTo(sourceFolder).path + if (file.isDirectory) "/" else ""
                zipOut.putNextEntry(ZipEntry(relativePath))
                if (file.isFile) file.inputStream().copyTo(zipOut)
                zipOut.closeEntry()
            }
        }

        IhLogger.log("<<[!]>> Архив ${outputZip.name} создан успешно.")
    }


    fun pasteNonStructured(sourcePath: Path) {
        if (!Files.exists(sourcePath)) { IhLogger.log("[!] Путь не найден: $sourcePath", type = MessageType.ERROR); return }
        Files.walk(sourcePath).forEach { currentPath ->
            val targetPath = path.resolve(sourcePath.relativize(currentPath))
            if (Files.isDirectory(currentPath)) {
                Files.createDirectories(targetPath)
            } else {
                Files.copy(currentPath, targetPath)
            }
        }
    }
}