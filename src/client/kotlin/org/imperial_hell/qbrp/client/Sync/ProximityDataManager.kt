package org.imperial_hell.qbrp.Sync

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Blocks.qbBlock
import org.imperial_hell.qbrp.Networking.Packets.PlayerDataPacket
import org.imperial_hell.qbrp.Networking.Packets.StringPacket
import org.imperial_hell.qbrp.Networking.PacketsList
import org.imperial_hell.qbrp.client.Network.ClientNetworkHandler.responseRequest
import org.imperial_hell.qbrp.client.Network.ClientReceiver
import java.util.UUID

class ProximityDataManager(
    private val player: ClientPlayerEntity,
    private val detectionRadius: Double = 32.0 // Радиус обнаружения игроков
) {
    // Локальное хранилище данных игроков в радиусе
    private val proximityPlayerDataMap: MutableMap<UUID, ProximityPlayerData> = mutableMapOf()
    private val blockDataMap: MutableMap<BlockPos, qbBlock> = mutableMapOf()

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
    }

    /**
     * Обрабатывает событие, когда игрок входит в радиус.
     */
    private fun onPlayerEnterRadius(playerUuid: UUID) {
        // Запрашиваем данные у сервера
        fetchPlayerData(playerUuid) { data ->
            if (data != null) {
                setPlayerData(data)
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
        println(playerUuid.toString())
        responseRequest(
            packetId = PacketsList.PLAYER_DATA_REQUEST,
            requestPacket = StringPacket(playerUuid.toString()),
            responseClass = PlayerDataPacket::class.java
        ) { response ->
            if (response is ProximityPlayerData) {
                callback(response)
            } else {
                callback(null)
            }
        }
    }


    fun registerReceiver() {
        ClientReceiver<ProximityPlayerData>(PacketsList.PLAYER_DATA) { data, context ->
            setPlayerData(data)
        }.register<PlayerDataPacket>()
    }

    fun setPlayerData(data: ProximityPlayerData) {
        data.clientHandle()
        proximityPlayerDataMap[UUID.fromString(data.playerUuid)] = data
    }

    /**
     * Получить данные игрока по UUID.
     */
    fun getPlayerData(playerUuid: UUID): ProximityPlayerData? {
        if (proximityPlayerDataMap[playerUuid] == null) {
            return null
            IhLogger.log("Данные игрока по UUID <<$playerUuid>> не найдены.", type = IhLogger.MessageType.ERROR)
        }
        return proximityPlayerDataMap[playerUuid] as ProximityPlayerData
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
