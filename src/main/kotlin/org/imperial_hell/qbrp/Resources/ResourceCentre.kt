package org.imperial_hell.qbrp.Resources

import com.google.gson.JsonObject
import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Files.IhConfig
import org.imperial_hell.qbrp.Resources.ContentUnits.PluginOverridesUnit
import org.imperial_hell.qbrp.Resources.Data.ModelData
import org.imperial_hell.qbrp.Resources.Data.Pack
import org.imperial_hell.qbrp.Resources.Data.PluginOverridesData
import org.imperial_hell.qbrp.Resources.Structure.PackStructure
import org.imperial_hell.qbrp.Resources.Structure.Parents
import org.imperial_hell.qbrp.Resources.Structure.Structure
import java.io.File

object ResourceCentre {
    val resources = Structure(IhConfig.SERVER_RESOURCES_PATH.toFile())
    val packStructure = PackStructure(IhConfig.SERVER_PACK_PATH.toFile())
    val pluginOverrides = resources.tree.addUnit(PluginOverridesData(), UnitKey("pluginOverrides"), "itemres") as PluginOverridesUnit
    val pack = ResourcePack(packStructure)

    fun bakeResourcePack() {
        pack.structure.addItemType(Identifier("qbrp", "custom_item_handheld"), Parents.HANDHELD, UnitKey("handheld"))
        pack.structure.addItemType(Identifier("qbrp", "custom_item_generated"), Parents.GENERATED, UnitKey("generated"))
        Baker.scanModels(IhConfig.SERVER_ITEM.toFile()).forEach {model -> pack.content.addModelBundle(ModelData(Baker.parseJson(model) as JsonObject), model.toPath());}
        pack.structure.initResourcePack(Pack(15, "Ресурс-пак qbrp"))
        packStructure.mod.pasteNonStructured(IhConfig.SERVER_OVERRIDE_PATH)
        pack.structure.save()
    }

}