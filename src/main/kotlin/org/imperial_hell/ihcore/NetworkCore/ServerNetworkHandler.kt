package org.imperial_hell.ihcore.server

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import org.imperial_hell.ihcore.NetworkCore.CharactersPacket
import org.imperial_hell.ihcore.NetworkCore.Packets.StringPacket
import org.imperial_hell.ihcore.NetworkCore.PacketsList
import org.imperial_hell.ihcore.NetworkCore.ServerPacketSender
import org.imperial_hell.ihcore.Ihcore
import org.imperial_hell.ihcore.Model.Character
import org.imperial_hell.ihcore.ChatTyping.ServerTypingBroadcaster

class ServerNetworkHandler(val serverCore: Ihcore) {

    // Регистрация обработчиков пакетов на сервере
    fun registerServer() {

        // Обработка запроса синхронизации
        ServerPlayNetworking.registerGlobalReceiver(PacketsList.SYNC_REQUEST) { server, player, handler, buf, responseSender ->
            val packet = StringPacket()
            packet.setBuffer(buf)
            val uuid = packet.read()
            // Обработка пакета на сервере
            serverCore.userManager.syncPlayer(player, uuid)
        }

        ServerPlayNetworking.registerGlobalReceiver(PacketsList.CHAT_TYPING) { server, player, handler, buf, responseSender ->
            ServerTypingBroadcaster.broadcastPlayerStartTyping(player)
        }
        ServerPlayNetworking.registerGlobalReceiver(PacketsList.END_TYPING) { server, player, handler, buf, responseSender ->
            ServerTypingBroadcaster.broadcastPlayerEndTyping(player)
        }
    }
}

