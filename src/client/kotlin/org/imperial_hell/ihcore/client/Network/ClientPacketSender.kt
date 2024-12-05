package org.imperial_hell.ihcore.client.Network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.ihcore.Networking.Packets.IhPacket

object ClientPacketSender {

    fun send(packetId: Identifier, data: IhPacket) {
        IhLogger.log("--> <<$packetId>> (${data.read()})", debugMode = true)
        ClientPlayNetworking.send(packetId, data.buf)
    }

}