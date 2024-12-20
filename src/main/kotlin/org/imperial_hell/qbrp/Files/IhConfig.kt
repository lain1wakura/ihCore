package org.imperial_hell.qbrp.Files

import java.nio.file.Paths

object IhConfig {

    const val DOWNLOAD_URL: String = "http://imperialhell.org:25008/resources.zip"
    const val DOWNLOAD_DIR: String = "assets/qbrp/downloaded"
    val PACKS_DIR = Paths.get("resourcepacks").toAbsolutePath()
    val PACK_DIR = Paths.get("resourcepacks/qbrp").toAbsolutePath()

    val DISTRIBUTION_PACK_PATH = Paths.get("qbrpres/resources.zip").toAbsolutePath()
    val SERVER_ITEM = Paths.get("qbrpres/items")
    val SERVER_RESOURCES_PATH = Paths.get("qbrpres")
    val SERVER_BAKED_PATH= Paths.get("qbrpres/baked")
    val SERVER_PACK_PATH= Paths.get("qbrpres/baked/qbrp")
    val SERVER_OVERRIDE_PATH= Paths.get("qbrpres/overrides")
    val SERVER_PACK_CONTENT_PATH= Paths.get("qbrpres/baked/qbrp/assets/qbrp")
}