package org.imperial_hell.qbrp.System

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import org.imperial_hell.qbrp.Networking.Packets.PlayerDataPacket
import org.imperial_hell.qbrp.Networking.PacketsList
import org.imperial_hell.qbrp.Networking.ServerPacketSender
import kotlin.collections.forEach

object PlayerDataBroadcaster {

    var LOCAL_BROADCAST_DISTANCE = 32.0

    fun localBroadcast(player: ServerPlayerEntity, data: PlayerDataPacket) {
        val visiblePlayers = getVisiblePlayersFor(player, LOCAL_BROADCAST_DISTANCE)
        visiblePlayers.forEach { visiblePlayer ->
            ServerPacketSender.send(visiblePlayer, PacketsList.PLAYER_DATA, data)
        }
    }

    fun getVisiblePlayersFor(player: ServerPlayerEntity, radius: Double): List<ServerPlayerEntity> {
        // Получаем мир, в котором находится игрок
        val world = player.world as ServerWorld

        val visiblePlayers = world.players.filter { otherPlayer ->
            otherPlayer !== player &&// исключаем самого игрока
                    otherPlayer.squaredDistanceTo(player) <= radius * radius // проверяем дистанцию
        }
        return visiblePlayers
    }

}