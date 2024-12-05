package org.imperial_hell.ihcore.Sync

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.ihcore.Networking.Packets.PlayerDataPacket
import org.imperial_hell.ihcore.Networking.Packets.StringPacket
import org.imperial_hell.ihcore.Networking.PacketsList
import org.imperial_hell.ihcore.client.Network.ClientNetworkHandler
import org.imperial_hell.ihcore.client.Network.ClientReceiver
import java.util.UUID

class ProximityDataManager(
    private val player: ClientPlayerEntity,
    private val detectionRadius: Double = 32.0 // Радиус обнаружения игроков
) {
    // Локальное хранилище данных игроков в радиусе
    private val proximityPlayerDataMap: MutableMap<UUID, ProximityPlayerData> = mutableMapOf()

    /**
     * Обновляет состояние игроков в радиусе:
     * - Добавляет новых игроков.
     * - Удаляет игроков, которые покинули радиус.
     */
    fun updateProximityData() {
        val currentNearbyPlayers = findNearbyPlayers().map { it.uuid }
        proximityPlayerDataMap.values.forEach { value -> println(value) }

        // Определяем новых игроков
        val newPlayers = currentNearbyPlayers - proximityPlayerDataMap.keys
        for (uuid in newPlayers) {
            onPlayerEnterRadius(uuid)
        }

        // Определяем игроков, которые покинули радиус
        val removedPlayers = proximityPlayerDataMap.keys - currentNearbyPlayers
        for (uuid in removedPlayers) {
            onPlayerExitRadius(uuid)
        }

        IhLogger.log("$proximityPlayerDataMap", debugMode = true)
    }

    /**
     * Обрабатывает событие, когда игрок входит в радиус.
     */
    private fun onPlayerEnterRadius(playerUuid: UUID) {
        // Запрашиваем данные у сервера
        fetchPlayerData(playerUuid) { data ->
            if (data != null) {
                proximityPlayerDataMap[playerUuid] = data
                println("Игрок $playerUuid добавлен в радиус с данными: $data")
            } else {
                println("Не удалось получить данные игрока $playerUuid")
            }
        }
    }

    /**
     * Обрабатывает событие, когда игрок выходит из радиуса.
     */
    private fun onPlayerExitRadius(playerUuid: UUID) {
        proximityPlayerDataMap.remove(playerUuid)
        println("Игрок $playerUuid вышел из радиуса и его данные удалены.")
    }

    /**
     * Возвращает список игроков в радиусе.
     */
    private fun findNearbyPlayers(): List<PlayerEntity> {
        return player.world.players.filter {
            it != player && it.squaredDistanceTo(player) <= detectionRadius * detectionRadius
        }
    }

    /**
     * Асинхронно запрашивает данные игрока у сервера.
     */
    private fun fetchPlayerData(playerUuid: UUID, callback: (ProximityPlayerData?) -> Unit) {
        val response = ClientNetworkHandler.responseRequest(
            packetId = PacketsList.PLAYER_DATA_REQUEST,
            requestPacket = StringPacket(playerUuid.toString()),
            responseClass = PlayerDataPacket::class.java
        )

        if (response is ProximityPlayerData) {
            callback(response)
        } else {
            callback(null)
        }
    }

    fun registerReceiver() {
        ClientReceiver<ProximityPlayerData>(PacketsList.PLAYER_DATA) { data, context ->
            setPlayerData(data)
        }.register<PlayerDataPacket>()
    }

    fun setPlayerData(data: ProximityPlayerData) {
        proximityPlayerDataMap[UUID.fromString(data.playerUuid)] = data
    }

    /**
     * Получить данные игрока по UUID.
     */
    fun getPlayerData(playerUuid: UUID): ProximityPlayerData? {
        return proximityPlayerDataMap[playerUuid]
    }

    /**
     * Удалить данные игрока по UUID.
     */
    fun removePlayerData(playerUuid: UUID) {
        proximityPlayerDataMap.remove(playerUuid)
    }

    /**
     * Получить всех игроков в радиусе.
     */
    fun getAllPlayerData(): Map<UUID, ProximityPlayerData> {
        return proximityPlayerDataMap.toMap()
    }
}
