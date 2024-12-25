package org.imperial_hell.qbrp.Resources.Structure

import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.ContentUnits.ModelUnit
import org.imperial_hell.qbrp.Resources.Data.MetaData
import org.imperial_hell.qbrp.Resources.Data.ModelData
import org.imperial_hell.qbrp.Resources.Data.Pack
import org.imperial_hell.qbrp.Resources.Data.PredicatesData
import org.imperial_hell.qbrp.Resources.Data.TextureData
import org.imperial_hell.qbrp.Resources.UnitKey
import java.io.File
import java.nio.file.Path
import kotlin.collections.mutableMapOf
import kotlin.io.path.nameWithoutExtension

class PackStructure(path: File) : Structure(path) {

    val root = tree.addBranch("assets")
    val mod = tree.addBranch("qbrp")

    val modelsRegistry = mod.addBranch("models", key = Branches.MODELS_REGISTRY.key)
    val itemTypes = modelsRegistry.addBranch("item", key = Branches.ITEM_MODELS.key)
    val texturesRegistry = mod.addBranch("textures", key = Branches.TEXTURES_REGISTRY.key)
    val itemTextures = texturesRegistry.addBranch("item", key = Branches.ITEM_TEXTURES.key)

    fun initResourcePack(pack: Pack) {
        tree.addUnit(MetaData(pack), key = UnitKey("packMeta"), name = "pack")
    }

    fun addItemType(item: Identifier, parent: Parents, key: UnitKey) {
        registry(Branches.ITEM_MODELS.key).addUnit(
            PredicatesData(parent = parent.value), key, item.path)
    }

    fun addTexture(path: Path, key: UnitKey): ContentUnit {
        return registry(Branches.ITEM_TEXTURES.key).addContainer().addUnit(TextureData(path.toString()), key, path.nameWithoutExtension)
    }

    fun addModel(model: ModelData, key: UnitKey): ModelUnit {
        return registry(Branches.ITEM_MODELS.key).addContainer().addUnit(model, key, path.nameWithoutExtension) as ModelUnit
    }

}