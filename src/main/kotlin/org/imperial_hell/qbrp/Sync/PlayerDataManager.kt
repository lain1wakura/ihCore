package org.imperial_hell.qbrp.Sync

import net.minecraft.server.MinecraftServer
import java.util.UUID
import org.imperial_hell.common.Packets.PlayerDataPacket
import org.imperial_hell.common.Proxy.ProximityPlayerData
import org.imperial_hell.qbrp.System.PlayerDataBroadcaster

class PlayerDataManager(val server: MinecraftServer) {

    // Хранение данных игроков по их UUID
    private val proximityPlayerDataMap: MutableMap<UUID, ProximityPlayerData> = mutableMapOf()

    // Метод для получения данных игрока по UUID
    fun getPlayerData(playerUuid: UUID): ProximityPlayerData? {
        return proximityPlayerDataMap[playerUuid]
    }

    // Метод для обновления данных игрока
    fun updatePlayerData(playerUuid: UUID, newData: ProximityPlayerData) {
        // Если данные игрока существуют, обновляем их, если нет — создаём новые
        proximityPlayerDataMap[playerUuid] = newData
        println("Добавлен игрок с UUID ${playerUuid.toString()} | $proximityPlayerDataMap")

        // После изменения данных отправляем их через PlayerDataBroadcaster
        sendPlayerDataUpdate(playerUuid, newData)
    }

    fun updatePlayerDataAttribute(playerUuid: UUID, attribute: (ProximityPlayerData) -> ProximityPlayerData) {
        val existingData = proximityPlayerDataMap[playerUuid]
        if (existingData != null) {
            // Создаем обновленные данные, применяя функцию attribute
            val updatedData = attribute(existingData)

            // Сохраняем обновленные данные
            proximityPlayerDataMap[playerUuid] = updatedData

            // Отправляем обновленные данные
            sendPlayerDataUpdate(playerUuid, updatedData)
        }
    }

    // Метод для отправки обновленных данных игрока
    private fun sendPlayerDataUpdate(uuid: UUID, newData: ProximityPlayerData) {
        // Создаем пакет для отправки данных игрока
        val playerDataPacket = PlayerDataPacket(newData)
        val player = server.playerManager.getPlayer(uuid)
        println("Поиск по UUID $uuid | $proximityPlayerDataMap")

        if (player != null) {
            // Игрок найден, можно безопасно привести к ServerPlayerEntity
            PlayerDataBroadcaster.localBroadcast(player, playerDataPacket)
        } else {
            // Игрок не найден, возможно, он еще не подключился или вышел
            println("Игрок с UUID $uuid не найден на сервере. | $proximityPlayerDataMap")
        }
    }
}
