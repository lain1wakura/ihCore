package org.imperial_hell.ihcore.ChatTyping
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import org.imperial_hell.ihcore.NetworkCore.Packets.StringPacket
import org.imperial_hell.ihcore.NetworkCore.PacketsList
import org.imperial_hell.ihcore.NetworkCore.ServerPacketSender

object ServerTypingBroadcaster {

    fun broadcastPlayerStartTyping(player: ServerPlayerEntity) {
        val visiblePlayers = getVisiblePlayersFor(player, 32.0)
        visiblePlayers.forEach { visiblePlayer ->
            ServerPacketSender.send(visiblePlayer, PacketsList.CHAT_TYPING, StringPacket(player.uuid.toString()))
        }
    }

    fun broadcastPlayerEndTyping(player: ServerPlayerEntity) {
        val visiblePlayers = getVisiblePlayersFor(player, 32.0)
        visiblePlayers.forEach { visiblePlayer ->
            ServerPacketSender.send(visiblePlayer, PacketsList.END_TYPING, StringPacket(player.uuid.toString()))
        }
    }


    fun getVisiblePlayersFor(player: ServerPlayerEntity, radius: Double): List<ServerPlayerEntity> {
        // Получаем мир, в котором находится игрок
        val world = player.world as ServerWorld

        // Ищем игроков в заданном радиусе от координат выбранного игрока
        val visiblePlayers = world.players.filter { otherPlayer ->
            otherPlayer !== player && // исключаем самого игрока
                    otherPlayer.squaredDistanceTo(player) <= radius * radius // проверяем дистанцию
        }
        return visiblePlayers
    }



}