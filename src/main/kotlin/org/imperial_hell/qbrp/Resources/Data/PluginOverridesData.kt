package org.imperial_hell.qbrp.Resources.Data

import com.google.gson.annotations.SerializedName
import klite.jdbc.In
import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.ContentUnits.PluginOverridesUnit
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

class PluginOverridesData(val predicates: MutableList<Predicate> = mutableListOf<Predicate>()): RawData {
    override fun convert(
        path: Path,
        key: UnitKey,
        name: String
    ): ContentUnit {
        return PluginOverridesUnit(path, key, name, this)
    }

    fun addPredicate(path: String, customModelData: Int) {
        predicates.add(Predicate(path, customModelData))
    }

    data class Predicate(
        val path: String,
        @SerializedName("custom_model_data")
        val customModelData: Int
    )
}