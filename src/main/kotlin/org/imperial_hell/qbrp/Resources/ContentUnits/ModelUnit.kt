package org.imperial_hell.qbrp.Resources.ContentUnits
import com.google.gson.Gson
import org.imperial_hell.qbrp.Resources.Data.ModelData
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Files
import java.nio.file.Path

class ModelUnit(
    path: Path,
    key: UnitKey,
    name: String,
    val data: ModelData
) : ContentUnit(path, name, "json", key) {

    override fun save() {
        val gson = Gson()
        Files.writeString(path, gson.toJson(data.json)) // Сохраняем обновлённый JSON
    }

}