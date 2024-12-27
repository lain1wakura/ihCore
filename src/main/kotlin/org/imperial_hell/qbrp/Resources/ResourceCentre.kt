package org.imperial_hell.qbrp.Resources

import com.google.gson.*
import com.google.gson.JsonObject
import net.minecraft.util.Identifier
import org.imperial_hell.qbrp.Game.Items.BaseItem
import org.imperial_hell.qbrp.System.Files.IhConfig
import org.imperial_hell.qbrp.Resources.Data.ModelData
import org.imperial_hell.qbrp.Resources.Data.Pack
import org.imperial_hell.qbrp.Resources.Data.PluginOverridesData
import org.imperial_hell.qbrp.Resources.Structure.PackStructure
import org.imperial_hell.qbrp.Resources.Structure.Parents
import org.imperial_hell.qbrp.Resources.Structure.Structure
import java.io.File
import java.nio.file.Path

object ResourceCentre {
    val resources = Structure(IhConfig.SERVER_RESOURCES_PATH.toFile())
    val pluginOverrides = resources.tree.addUnit(PluginOverridesData(), UnitKey("pluginOverrides"), "itemres", "json")
    val pack = ResourcePack(
        PackStructure(IhConfig.SERVER_PACK_PATH.toFile())
    )

    fun createGsonForPathType(): Gson {
        return GsonBuilder()
            .registerTypeHierarchyAdapter(File::class.java, FileAdapter())
            .registerTypeHierarchyAdapter(Path::class.java, DynamicPathAdapter()) // Регистрируем адаптер для конкретного типа Path
            .create()
    }

    fun bakeResourcePack(items: List<BaseItem>) {
        items.forEach { pack.structure.addItemType(it.identifier, it.modelType, UnitKey("item_${it.name}")) }
        Baker.scanModels(IhConfig.SERVER_ITEM.toFile()).forEach { model ->
            pack.content.addModelBundle(ModelData(Baker.parseJson(model) as JsonObject), model.toPath())
        }
        pack.structure.initResourcePack(Pack(15, "Ресурс-пак qbrp"))
        pack.structure.mod.pasteNonStructured(IhConfig.SERVER_OVERRIDE_PATH)
        pack.finish(IhConfig.DISTRIBUTION_PACK_PATH.toFile()) { pack.structure.save() }
    }
}
