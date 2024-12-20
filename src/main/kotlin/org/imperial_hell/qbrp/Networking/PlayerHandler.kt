package org.imperial_hell.qbrp.Networking
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import org.imperial_hell.qbrp.Characters.System.UserManager
import org.imperial_hell.common.Proxy.ProxyPlayerData

class PlayerHandler(
    val userManager: UserManager,
    ) {

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
        println("UUID: ${player.uuid}")
        userManager.playerDataManager.updatePlayerData(player.uuid, ProxyPlayerData.getBlankPlayerData(player.uuidAsString))

        val uuid = userManager.dbService.getUserUuid(player.name.string) as String
        if (uuid == "Undefined") {
            //ServerPacketSender.send(player, PacketsList.SYNC_REQUEST, SignalPacket())
        } else {
            userManager.syncPlayer(player, uuid)
        }
    }

    fun onPlayerLeave(player: ServerPlayerEntity) {
        val uuid = userManager.dbService.getUserUuid(player.name.string)
        if (uuid != "Undefined") {
            userManager.characterManager.removeCharacter(uuid as String)
            userManager.characterManager.saveAllCharacters()
        }
    }

}