package org.imperial_hell.qbrp.server

import org.imperial_hell.qbrp.Characters.System.UserManager
import org.imperial_hell.common.PacketsList
import org.imperial_hell.common.Packets.PlayerDataPacket
import org.imperial_hell.common.Packets.Signal
import org.imperial_hell.common.Packets.SignalPacket
import org.imperial_hell.common.Packets.StringPacket
import org.imperial_hell.qbrp.Networking.ServerPacketSender
import org.imperial_hell.common.Proxy.ProximityPlayerData
import java.util.UUID
import org.imperial_hell.qbrp.Networking.ServerReceiver

class ServerNetworkHandler(val userManager: UserManager) {

    // Регистрация обработчиков пакетов на сервере
    fun registerServer() {

        // Используем Receiver для обработки запроса синхронизации
        ServerReceiver<String>(PacketsList.SYNC_REQUEST) { data, context ->
            // Обработка пакета на сервере
            userManager.syncPlayer(context.player, data)
        }.register<SignalPacket>()

        // Используем Receiver для обработки запроса данных игрока
        ServerReceiver<String>(PacketsList.PLAYER_DATA_REQUEST) { data, context ->
            println(data)
            val dataUUID = UUID.fromString(data)
            println(dataUUID)
            val playerData = userManager.playerDataManager.getPlayerData(dataUUID)
            println(playerData)
            if (playerData != null) {
                ServerPacketSender.send(context.player, PacketsList.PLAYER_DATA_REQUEST, PlayerDataPacket(playerData))
            }
        }.register<StringPacket>()

        // Используем Receiver для обработки пакета CHAT_TYPING
        ServerReceiver<Signal>(PacketsList.CHAT_TYPING) { data, context ->
            userManager.playerDataManager.updatePlayerDataAttribute(context.player.uuid) { currentData ->
                currentData.copy(state = ProximityPlayerData.State.TYPING_REPLICA)
            }
        }.register<SignalPacket>()

        // Используем Receiver для обработки пакета END_TYPING
        ServerReceiver<Signal>(PacketsList.END_TYPING) { data, context ->
            userManager.playerDataManager.updatePlayerDataAttribute(context.player.uuid) { currentData ->
                currentData.copy(state = ProximityPlayerData.State.NONE)
            }
        }.register<SignalPacket>()
    }
}
