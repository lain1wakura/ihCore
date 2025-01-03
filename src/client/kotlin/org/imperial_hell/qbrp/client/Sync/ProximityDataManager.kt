package org.imperial_hell.qbrp.Sync

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.common.Blocks.qbBlock
import org.imperial_hell.common.Packets.PlayerDataPacket
import org.imperial_hell.common.Packets.StringPacket
import org.imperial_hell.common.PacketsList
import org.imperial_hell.common.Proxy.ProxyPlayerData
import org.imperial_hell.qbrp.Utils.qbTimer
import org.imperial_hell.qbrp.client.Network.ClientNetworkHandler.responseRequest
import org.imperial_hell.qbrp.client.Network.ClientReceiver
import java.util.UUID

class ProximityDataManager(
    private val player: ClientPlayerEntity,
    private val detectionRadius: Double = 32.0 // Радиус обнаружения игроков
) {
    // Локальное хранилище данных игроков в радиусе
    private val proxyPlayerDataMap: MutableMap<UUID, ProxyPlayerData> = mutableMapOf()
    private val blockDataMap: MutableMap<BlockPos, qbBlock> = mutableMapOf()
    private val proxyUpdateCycle = qbTimer(20) { updateProximityData() }.start()

    /**
     * Обновляет состояние игроков в радиусе:
     * - Добавляет новых игроков.
     * - Удаляет игроков, которые покинули радиус.
     */
    fun updateProximityData() {
        val currentNearbyPlayers = findNearbyPlayers().map { it.uuid }
        proxyPlayerDataMap.values.forEach { value -> println(value) }

        // Определяем новых игроков
        val newPlayers = currentNearbyPlayers - proxyPlayerDataMap.keys
        for (uuid in newPlayers) {
            onPlayerEnterRadius(uuid)
        }

        // Определяем игроков, которые покинули радиус
        val removedPlayers = proxyPlayerDataMap.keys - currentNearbyPlayers
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
        proxyPlayerDataMap.remove(playerUuid)
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
    private fun fetchPlayerData(playerUuid: UUID, callback: (ProxyPlayerData?) -> Unit) {
        println(playerUuid.toString())
        responseRequest(
            packetId = PacketsList.PLAYER_DATA_REQUEST,
            requestPacket = StringPacket(playerUuid.toString()),
            responseClass = PlayerDataPacket::class.java
        ) { response ->
            if (response is ProxyPlayerData) {
                callback(response)
            } else {
                callback(null)
            }
        }
    }


    fun registerReceiver() {
        ClientReceiver<ProxyPlayerData>(PacketsList.PLAYER_DATA) { data, context ->
            setPlayerData(data)
        }.register<PlayerDataPacket>()
    }

    fun setPlayerData(data: ProxyPlayerData) {
        data.clientHandle()
        proxyPlayerDataMap[UUID.fromString(data.playerUuid)] = data
    }

    /**
     * Получить данные игрока по UUID.
     */
    fun getPlayerData(playerUuid: UUID): ProxyPlayerData? {
        if (proxyPlayerDataMap[playerUuid] == null) {
            return null
            IhLogger.log("Данные игрока по UUID <<$playerUuid>> не найдены.", type = IhLogger.MessageType.ERROR)
        }
        return proxyPlayerDataMap[playerUuid] as ProxyPlayerData
    }

    /**
     * Удалить данные игрока по UUID.
     */
    fun removePlayerData(playerUuid: UUID) {
        proxyPlayerDataMap.remove(playerUuid)
    }

    /**
     * Получить всех игроков в радиусе.
     */
    fun getAllPlayerData(): Map<UUID, ProxyPlayerData> {
        return proxyPlayerDataMap.toMap()
    }
}
