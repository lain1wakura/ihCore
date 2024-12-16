package org.imperial_hell.qbrp.Sync.HTTP

import klite.AssetsHandler
import klite.Server
import java.nio.file.Path
class ResourceServer() {

    lateinit var server: Server

    fun init() {
        server = Server().apply {
            assets("/", AssetsHandler(Path.of("qbrpres")))
            start()
        }
    }

    fun stop() {
        server.stop()
    }
}
