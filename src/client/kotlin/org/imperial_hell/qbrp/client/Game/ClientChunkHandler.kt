package org.imperial_hell.qbrp.client.Game

import kotlinx.serialization.json.JsonObject
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import org.imperial_hell.common.Packets.StringPacket
import org.imperial_hell.common.PacketsList
import org.imperial_hell.common.Proxy.ProxyBlockData
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.client.Network.ClientNetworkHandler

class ClientChunkHandler {

    fun register() {
        // Регистрация обработчика загрузки чанков
        ClientChunkEvents.CHUNK_LOAD.register { world, chunk ->
            //onChunkLoad(world, chunk)
        }

        // Регистрация обработчика выгрузки чанков
        ClientChunkEvents.CHUNK_UNLOAD.register { world, chunk ->
            onChunkUnload(world, chunk)
        }
    }

    private fun onChunkLoad(world: World, chunk: Chunk) {
        IhLogger.log(
            "Чанк загружен: (${chunk.pos.x}, ${chunk.pos.z})",
            IhLogger.MessageType.INFO, debugMode = true
        )
        val blocksData = ClientNetworkHandler.responseRequest(PacketsList.CHUNK_DATA, StringPacket("${chunk.pos.centerX}||${chunk.pos.centerZ}"),
            JsonObject::class.java) { response ->
        }
    }

    private fun onChunkUnload(world: World, chunk: Chunk) {
        IhLogger.log(
            "Чанк выгружен: (${chunk.pos.x}, ${chunk.pos.z})",
            IhLogger.MessageType.INFO, debugMode = true
        )
    }
}