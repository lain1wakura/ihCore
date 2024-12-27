package org.imperial_hell.qbrp.Resources.Structure

import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.ISavable
import org.imperial_hell.qbrp.Resources.UnitKey
import java.io.File
import java.nio.file.Path
import kotlin.collections.mutableMapOf
import kotlin.io.path.nameWithoutExtension

open class Structure(val path: File) {

    var branchesRegistry: MutableMap<UnitKey, Branch> = mutableMapOf()
    var contentRegistry: MutableMap<UnitKey, ContentUnit> = mutableMapOf()

    val tree = Branch(path.toPath()).structure(this) as Branch

    fun registerBranch(branch: Branch) {
        if (branch.key != Branches.UNSET.key) {
            branchesRegistry[branch.key] = branch
        }
    }

    fun registerContent(contentUnit: ContentUnit) {
        if (contentRegistry.containsKey(contentUnit.key)) {
            IhLogger.log("<<[!]>> Замена ключа ContentUnit")
        }
        contentRegistry[contentUnit.key] = contentUnit
    }

    fun registry(key: UnitKey): Branch { return branchesRegistry[key]!! }

    fun content(key: UnitKey): ContentUnit { return contentRegistry[key]!! }

    fun save(branch: Branch = tree): Boolean {
        try {
            branch.children.forEach { child ->
                when (child) {
                    is ISavable -> {
                        child.save() // Сохраняем ContentUnit
                    }

                    is Branch -> {
                        save(child) // Рекурсивно обходим вложенные Branch
                    }
                }
            }
            return true
        }
        catch(e: Exception) {
            IhLogger.log("Возникла ошибка при сборке пакета ресурсов: ${e.stackTrace}")
            return false
        }
        return false
    }
}