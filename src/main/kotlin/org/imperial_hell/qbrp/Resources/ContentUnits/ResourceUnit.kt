package org.imperial_hell.qbrp.Resources.ContentUnits
import org.imperial_hell.qbrp.Resources.Structure.Structure
import java.nio.file.Path

abstract class ResourceUnit(
    var path: Path,
    var structure: Structure? = null) {

    fun structure(pack: Structure): ResourceUnit {
        structure = pack
        return this
    }

    open fun handle(): ResourceUnit {
        return this
    }

}