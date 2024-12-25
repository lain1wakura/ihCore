package org.imperial_hell.qbrp.Resources.Data

import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.ContentUnits.PredicatesUnit
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

data class PredicatesData(
    val parent: String,
    val textures: Textures = Textures("qbrp:item/placeholder"),
    val overrides: MutableList<Override> = mutableListOf<Override>() // По умолчанию пустой список
): RawData {

    fun addPredicate(modelPath: String, modelData: Int): Override =
        Override(Predicate(modelData), modelPath).also { overrides.add(it) }

    override fun convert(path: Path, key: UnitKey, name: String): ContentUnit {
        return PredicatesUnit(path, key = key, name, this)
    }
}

data class Textures(
    val layer0: String
)

data class Override(
    val predicate: Predicate,
    val model: String
)

data class Predicate(
    val custom_model_data: Int
)
