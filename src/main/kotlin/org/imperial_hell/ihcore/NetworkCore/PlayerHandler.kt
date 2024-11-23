package org.imperial_hell.ihcore.NetworkCore
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import org.imperial_hell.ihcore.Characters.Character
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
        if (server.databaseManager.isPlayerNicknameInDatabase(player.name.string) == true) {
            val profile = server.databaseManager.getProfileByNickname(player.name.string)
            val character = server.playerManager.createCharacterFromDatabase(profile[2]) ?: {}
            server.playerManager.applyCharacter(player, character as Character)

        } else {
            //ServerPacketSender.send(player, PacketsList.SYNC_REQUEST, SignalPacket())
        }

    }

    fun onPlayerLeave(player: ServerPlayerEntity) {
        if (server.databaseManager.isPlayerNicknameInDatabase(player.name.string) == true) {
            val uuid = server.databaseManager.getProfileByNickname(player.name.string)[0]
            server.databaseManager.updatePlayerProfile(server.playerManager.getPlayer(uuid) as Character)
            server.playerManager.removePlayer(uuid)
        }
    }

}