package org.imperial_hell.qbrp.Resources.Data
import com.google.gson.annotations.SerializedName

class PluginOverridesData(val predicates: MutableList<Predicate> = mutableListOf<Predicate>()): RawData() {
    fun addPredicate(path: String, customModelData: Int) {
        predicates.add(Predicate(path, customModelData))
    }

    data class Predicate(
        val path: String,
        @SerializedName("custom_model_data")
        val customModelData: Int
    )
}