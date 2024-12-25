package org.imperial_hell.qbrp.Resources.Data

import com.google.gson.annotations.SerializedName
import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.ContentUnits.MetaUnit
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

// Класс, представляющий вложенный объект "pack"
data class Pack(
    @SerializedName("pack_format")
    val packFormat: Int, // Используем правильное имя поля, чтобы оно соответствовало JSON

    @SerializedName("description")
    val description: String
)

// Основной класс MetaData с вложенным объектом "pack"
data class MetaData(
    val pack: Pack // Вложенная структура
) : RawData {
    override fun convert(
        path: Path,
        key: UnitKey,
        name: String
    ): ContentUnit {
        return MetaUnit(path, key, name, this)
    }
}
