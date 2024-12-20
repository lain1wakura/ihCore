package org.imperial_hell.qbrp.Sync.HTTP

import klite.AssetsHandler
import klite.Server
import org.imperial_hell.ihSystems.IhLogger
import java.net.InetSocketAddress
import java.nio.file.Path
class ResourceServer() {

    lateinit var server: Server

    fun init() {
        server = Server(listen = InetSocketAddress(25008)).apply {
            assets("/", AssetsHandler(Path.of("qbrpres")))
            start()
        }
        IhLogger.log("Serevr: ${server.listen.hostString}, ${server.listen.hostName}, ${server.listen.address}, ${server.listen}")
    }

    fun stop() {
        server.stop()
    }
}
