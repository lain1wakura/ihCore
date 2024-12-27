package org.imperial_hell.qbrp.Resources.GameContent

import org.imperial_hell.qbrp.Resources.Data.ModelData
import org.imperial_hell.qbrp.Resources.Data.PluginOverridesData
import org.imperial_hell.qbrp.Resources.Data.PredicatesData
import org.imperial_hell.qbrp.Resources.PackContent
import org.imperial_hell.qbrp.Resources.ResourceCentre
import org.imperial_hell.qbrp.Resources.UnitKey
import org.imperial_hell.qbrp.Resources.PathFormat.*
import org.imperial_hell.qbrp.Resources.Structure.PackStructure
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

class ModelBundle(
    model: ModelData,
    directory: Path,
    key: String = UUID.randomUUID().toString(),
    modelType: String = "unset",
    val packStructure: PackStructure
) {
    val modelDataId = PackContent.getModelData()
    val textures = loadTextures(model, directory.parent.toString())
    val modelUnit = packStructure.addModel(model, UnitKey(key)).apply {
        (data as ModelData).processTextures(textures)
        save()
    }

    val override = (packStructure.content(UnitKey("item_custom_item_${modelType}")).data as PredicatesData)
        .addPredicate(modelUnit.path.toString().getRelative("models"), modelDataId)

    val pluginData = (ResourceCentre.pluginOverrides.data as PluginOverridesData)
        .addPredicate(directory.toString().getRelative("items").toJsonFormat(), modelDataId)
        .also { ResourceCentre.pluginOverrides.save() }

    fun loadTextures(model: ModelData, modelPath: String): List<String> {
        return model.textures.values.map { texture ->
            val unitKey = UnitKey(texture.split(":").last())
            val textureUnit = packStructure.addTexture(
                Paths.get(
                    "qbrpres",
                    texture.replace(":", "/")
                        .replace("./", modelPath + "/")
                        .replace("qbrpres", "") + ".png"
                ), key = unitKey
            )
            "qbrp:" + textureUnit.path.toString().getRelative("item").toJsonFormat()
        }
    }
}
