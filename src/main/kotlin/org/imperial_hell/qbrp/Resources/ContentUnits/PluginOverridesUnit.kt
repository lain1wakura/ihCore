package org.imperial_hell.qbrp.Resources.ContentUnits

import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON
import org.imperial_hell.qbrp.Resources.Data.PluginOverridesData
import org.imperial_hell.qbrp.Resources.Data.TextureData
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

class PluginOverridesUnit(path: Path,
                          key: UnitKey = UnitKey("unset"),
                          val name: String,
                          val data: PluginOverridesData)
    : ContentUnit(path, name, "json", key) {

    override fun save() {
        path.toFile().writeText(GSON.toJson(data.predicates))
    }
}