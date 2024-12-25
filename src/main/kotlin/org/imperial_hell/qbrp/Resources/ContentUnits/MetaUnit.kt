package org.imperial_hell.qbrp.Resources.ContentUnits

import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON
import org.imperial_hell.qbrp.Resources.Data.MetaData
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

class MetaUnit(path: Path, key: UnitKey = UnitKey("packMeta"), val name: String = "pack", val data: MetaData) : ContentUnit(path, name, "mcmeta", key = key) {

    override fun save() {
        path.toFile().writeText(GSON.toJson(data))
    }

}