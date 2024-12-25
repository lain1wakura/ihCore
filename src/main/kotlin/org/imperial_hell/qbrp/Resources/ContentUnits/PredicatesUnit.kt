package org.imperial_hell.qbrp.Resources.ContentUnits

import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON
import org.imperial_hell.qbrp.Resources.Data.PredicatesData
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

class PredicatesUnit(path: Path, key: UnitKey = UnitKey("unset"), val name: String, val data: PredicatesData) : ContentUnit(path, name, "json", key = key) {

    override fun save() {
        path.toFile().writeText(GSON.toJson(data))
    }

}