package org.imperial_hell.qbrp.Resources.Data

import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.ContentUnits.TextureUnit
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

data class TextureData(
    val texturePath: String // Путь к текстуре, например "textures/items/my_item.png"
) : RawData {

    override fun convert(
        path: Path,
        key: UnitKey,
        name: String
    ): ContentUnit {
        return TextureUnit(path, key, name, this)
    }
}