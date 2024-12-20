package org.imperial_hell.qbrp.System

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import org.imperial_hell.common.Packets.IhPacket
import org.imperial_hell.qbrp.Networking.ServerPacketSender
import kotlin.collections.forEach

object ServerBroadcaster {
     private val localBroadcastDistance: Double = 32.0 // Радиус локальной передачи

    /**
     * Локальная передача пакета: отправляет пакет всем видимым игрокам в заданном радиусе.
     */
    fun <T : IhPacket> broadcastToLocal(player: ServerPlayerEntity, packetType: Identifier, packet: T) {
        val visiblePlayers = getVisiblePlayersFor(player, localBroadcastDistance)
        visiblePlayers.forEach { visiblePlayer ->
            ServerPacketSender.send(visiblePlayer, packetType, packet)
        }
    }

    /**
     * Глобальная передача пакета: отправляет пакет всем игрокам на сервере.
     */
    fun <T : IhPacket> broadcastToAll(world: ServerWorld, packetType: Identifier, packet: T) {
        world.players.forEach { player ->
            ServerPacketSender.send(player, packetType, packet)
        }
    }

    /**
     * Локальная передача в квадратной области: отправляет пакет всем игрокам в пределах квадратного диапазона.
     */
    fun <T : IhPacket> broadcastToArea(
        world: ServerWorld,
        centerX: Double,
        centerZ: Double,
        radius: Double,
        packetType: Identifier,
        packet: T
    ) {
        val radiusSquared = radius * radius
        world.players.filter { player ->
            val dx = player.x - centerX
            val dz = player.z - centerZ
            dx * dx + dz * dz <= radiusSquared
        }.forEach { player ->
            ServerPacketSender.send(player, packetType, packet)
        }
    }

    /**
     * Получает список игроков, видимых для данного игрока в заданном радиусе.
     */
    private fun getVisiblePlayersFor(player: ServerPlayerEntity, radius: Double): List<ServerPlayerEntity> {
        val world = player.world as ServerWorld

        return world.players.filter { otherPlayer ->
            otherPlayer !== player && // Исключаем самого игрока
                    otherPlayer.squaredDistanceTo(player) <= radius * radius // Проверяем дистанцию
        }
    }
}
