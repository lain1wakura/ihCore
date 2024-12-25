package org.imperial_hell.qbrp.Resources.Structure

import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.ihSystems.IhLogger.MessageType
import org.imperial_hell.qbrp.Resources.Baker
import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.ContentUnits.ResourceUnit
import org.imperial_hell.qbrp.Resources.Data.RawData
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

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

    fun addUnit(data: RawData, key: UnitKey, name: String): ContentUnit {
        return add(data.convert(path, key, name)) as ContentUnit
    }

    fun add(unit: ResourceUnit): ResourceUnit {
        unit.structure = structure
        return unit.handle().also {
            IhLogger.log("<<[+]>> ${unit.path}")
            children.add(it)
        }
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