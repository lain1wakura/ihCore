package org.imperial_hell.qbrp.Resources

import org.imperial_hell.qbrp.Resources.Structure.PackStructure
import java.io.File

class ResourcePack(val structure: PackStructure) {

    val content = PackContent(structure)

}