/*package org.imperial_hell.qbrp.Sync

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource
import org.imperial_hell.qbrp.Files.IhConfig
import org.imperial_hell.common.Packets.SignalPacket
import org.imperial_hell.common.PacketsList
import org.imperial_hell.qbrp.Networking.ServerPacketSender

class ResourceInstruments() {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            net.minecraft.server.command.CommandManager.literal("itemres") // Specify the command name
            .executes { context ->
                ResourcePackBaker.process(IhConfig.SERVER_PACK_CONTENT_PATH, IhConfig.SERVER_ITEM)
                1 // Return a successful execution code
            }
        )
        dispatcher.register(
            net.minecraft.server.command.CommandManager.literal("loadres") // Specify the command name
                .executes { context ->
                    ServerPacketSender.send(context.source.player, PacketsList.LOADRES, SignalPacket())
                    1 // Return a successful execution code
                }
        )
    }
}
*/