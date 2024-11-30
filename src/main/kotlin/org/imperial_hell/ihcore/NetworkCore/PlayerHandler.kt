package org.imperial_hell.ihcore.NetworkCore
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import org.imperial_hell.ihcore.Model.Character
import org.imperial_hell.ihcore.Ihcore

class PlayerHandler(val server : Ihcore) {

    // Функция для инициализации события
    fun registerEvents() {
        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler, sender, server ->
            // Получаем объект игрока
            val player: ServerPlayerEntity = handler.player
            onPlayerJoin(player)
        })
        // Обработчик выхода игроков
        ServerPlayConnectionEvents.DISCONNECT.register(ServerPlayConnectionEvents.Disconnect { handler, server ->
            // Получаем объект игрока
            val player: ServerPlayerEntity = handler.player
            onPlayerLeave(player)
        })

    }

    // Обработка игрока, когда он зашел на сервер
    fun onPlayerJoin(player: ServerPlayerEntity) {
        val uuid = server.userService.getUserUuid(player.name.string) as String
        if (uuid == "Undefined") {
            //ServerPacketSender.send(player, PacketsList.SYNC_REQUEST, SignalPacket())
        } else {
            server.userManager.syncPlayer(player, uuid)
        }
    }

    fun onPlayerLeave(player: ServerPlayerEntity) {
        server.playerManager.saveAllCharacters()
    }

}