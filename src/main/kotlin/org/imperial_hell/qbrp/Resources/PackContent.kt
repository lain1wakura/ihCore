package org.imperial_hell.qbrp.Resources

import org.imperial_hell.qbrp.Resources.Data.ModelData
import org.imperial_hell.qbrp.Resources.PathFormat.getModelType
import org.imperial_hell.qbrp.Resources.GameContent.ModelBundle
import org.imperial_hell.qbrp.Resources.Structure.PackStructure
import java.util.UUID
import java.nio.file.Path

class PackContent(val packStructure: PackStructure) {

    val modelBundles = mutableListOf<ModelBundle>()

    fun addModelBundle(data: ModelData, path: Path, key: String = UUID.randomUUID().toString()) {
        modelBundles.add(ModelBundle(data, path, key, path.getModelType(), packStructure))
    }
    companion object {
        var data = 0
        fun getModelData(): Int { return data++ }
    }
}