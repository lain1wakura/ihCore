package org.imperial_hell.qbrp.Resources.ContentUnits
import com.sun.jna.platform.win32.Sspi
import org.imperial_hell.qbrp.Resources.Structure.Structure
import java.io.File
import java.nio.file.Files
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