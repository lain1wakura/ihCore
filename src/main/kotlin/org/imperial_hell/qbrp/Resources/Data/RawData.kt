package org.imperial_hell.qbrp.Resources.Data

import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.UnitKey
import java.nio.file.Path

// Превратить в метод и убрать это кривозубую конвертацию, сделав её красивенько через рефлексию в отдельном классе
interface RawData {
    fun convert(path: Path, key: UnitKey, name: String): ContentUnit
}