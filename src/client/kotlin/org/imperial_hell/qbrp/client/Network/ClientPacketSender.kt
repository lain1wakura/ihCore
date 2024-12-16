package org.imperial_hell.qbrp.client.Network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.common.Packets.IhPacket

object ClientPacketSender {

    fun send(packetId: Identifier, data: IhPacket) {
        data.write()
        IhLogger.log("--> <<$packetId>> (${data.read()})", debugMode = true)
        ClientPlayNetworking.send(packetId, data.buf)
    }

}