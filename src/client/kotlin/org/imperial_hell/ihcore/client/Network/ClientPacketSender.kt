package org.imperial_hell.ihcore.client.Network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.imperial_hell.ihcore.NetworkCore.Packets.IhPacket

object ClientPacketSender {

    fun send(packetId: Identifier, data: IhPacket) {
        println("Отправка на серве пакета ${packetId.path} с данными ${data.buf}")
        ClientPlayNetworking.send(packetId, data.buf)
    }

}