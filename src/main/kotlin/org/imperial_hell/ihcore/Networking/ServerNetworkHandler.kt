package org.imperial_hell.ihcore.server

import org.imperial_hell.ihcore.Networking.PacketsList
import org.imperial_hell.ihcore.Ihcore
import org.imperial_hell.ihcore.Networking.Packets.PlayerDataPacket
import org.imperial_hell.ihcore.Networking.Packets.Signal
import org.imperial_hell.ihcore.Networking.Packets.SignalPacket
import org.imperial_hell.ihcore.Networking.ServerPacketSender
import org.imperial_hell.ihcore.Sync.ProximityPlayerData
import java.util.UUID
import org.imperial_hell.ihcore.Networking.ServerReceiver

class ServerNetworkHandler(val serverCore: Ihcore) {

    // Регистрация обработчиков пакетов на сервере
    fun registerServer() {

        // Используем Receiver для обработки запроса синхронизации
        ServerReceiver<String>(PacketsList.SYNC_REQUEST) { data, context ->
            // Обработка пакета на сервере
            serverCore.userManager.syncPlayer(context.player, data)
        }.register<SignalPacket>()

        // Используем Receiver для обработки запроса данных игрока
        ServerReceiver<String>(PacketsList.PLAYER_DATA_REQUEST) { data, context ->
            val dataUUID = UUID.fromString(data)
            val playerData = serverCore.playerDataStorage.getPlayerData(dataUUID)
            if (playerData != null) {
                ServerPacketSender.send(context.player, PacketsList.PLAYER_DATA_REQUEST, PlayerDataPacket(playerData))
            }
        }.register<SignalPacket>()

        // Используем Receiver для обработки пакета CHAT_TYPING
        ServerReceiver<Signal>(PacketsList.CHAT_TYPING) { data, context ->
            serverCore.playerDataStorage.updatePlayerDataAttribute(context.player.uuid) { currentData ->
                currentData.copy(state = ProximityPlayerData.State.TYPING)
            }
        }.register<SignalPacket>()

        // Используем Receiver для обработки пакета END_TYPING
        ServerReceiver<Signal>(PacketsList.END_TYPING) { data, context ->
            serverCore.playerDataStorage.updatePlayerDataAttribute(context.player.uuid) { currentData ->
                currentData.copy(state = ProximityPlayerData.State.NONE)
            }
        }.register<SignalPacket>()
    }
}
