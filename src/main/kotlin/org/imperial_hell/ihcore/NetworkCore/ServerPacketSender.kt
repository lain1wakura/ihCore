package org.imperial_hell.ihcore.NetworkCore

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.imperial_hell.ihcore.NetworkCore.Packets.IhPacket

object ServerPacketSender {

    fun send(target: ServerPlayerEntity?, packetId: Identifier, data: IhPacket) {
        data.write()
        println("Отправка игроку ${target?.name} (${target?.uuid.toString()}) пакета ${packetId.path} с данными ${data.buf}")
        ServerPlayNetworking.send(target, packetId, data.buf)
    }

}