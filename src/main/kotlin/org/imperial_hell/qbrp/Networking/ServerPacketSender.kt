package org.imperial_hell.qbrp.Networking

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.common.Packets.IhPacket

object ServerPacketSender {

    fun send(target: ServerPlayerEntity?, packetId: Identifier, data: IhPacket) {
        data.write()
        ServerPlayNetworking.send(target, packetId, data.buf)
        IhLogger.log("${target?.name?.string} <-- <<$packetId>> (${data.read()})", debugMode = true)
    }

}