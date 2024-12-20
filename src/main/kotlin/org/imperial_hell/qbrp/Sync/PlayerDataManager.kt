package org.imperial_hell.qbrp.Sync

import net.minecraft.server.MinecraftServer
import java.util.UUID
import org.imperial_hell.common.Packets.PlayerDataPacket
import org.imperial_hell.common.PacketsList
import org.imperial_hell.common.Proxy.ProxyPlayerData
import org.imperial_hell.qbrp.System.ServerBroadcaster

class PlayerDataManager(val server: MinecraftServer) {

    // Хранение данных игроков по их UUID
    private val proxyPlayerDataMap: MutableMap<UUID, ProxyPlayerData> = mutableMapOf()

    // Метод для получения данных игрока по UUID
    fun getPlayerData(playerUuid: UUID): ProxyPlayerData? {
        return proxyPlayerDataMap[playerUuid]
    }

    // Метод для обновления данных игрока
    fun updatePlayerData(playerUuid: UUID, newData: ProxyPlayerData) {
        // Если данные игрока существуют, обновляем их, если нет — создаём новые
        proxyPlayerDataMap[playerUuid] = newData
        println("Добавлен игрок с UUID ${playerUuid.toString()} | $proxyPlayerDataMap")

        // После изменения данных отправляем их через PlayerDataBroadcaster
        sendPlayerDataUpdate(playerUuid, newData)
    }

    fun updatePlayerDataAttribute(playerUuid: UUID, attribute: (ProxyPlayerData) -> ProxyPlayerData) {
        val existingData = proxyPlayerDataMap[playerUuid]
        if (existingData != null) {
            // Создаем обновленные данные, применяя функцию attribute
            val updatedData = attribute(existingData)

            // Сохраняем обновленные данные
            proxyPlayerDataMap[playerUuid] = updatedData

            // Отправляем обновленные данные
            sendPlayerDataUpdate(playerUuid, updatedData)
        }
    }

    // Метод для отправки обновленных данных игрока
    private fun sendPlayerDataUpdate(uuid: UUID, newData: ProxyPlayerData) {
        // Создаем пакет для отправки данных игрока
        val playerDataPacket = PlayerDataPacket(newData)
        val player = server.playerManager.getPlayer(uuid)
        println("Поиск по UUID $uuid | $proxyPlayerDataMap")

        if (player != null) {
            // Игрок найден, можно безопасно привести к ServerPlayerEntity
            ServerBroadcaster.broadcastToLocal<PlayerDataPacket>(player, PacketsList.PLAYER_DATA, playerDataPacket)
        } else {
            // Игрок не найден, возможно, он еще не подключился или вышел
            println("Игрок с UUID $uuid не найден на сервере. | $proxyPlayerDataMap")
        }
    }
}
