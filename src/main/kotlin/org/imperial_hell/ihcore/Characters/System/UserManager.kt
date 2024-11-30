package org.imperial_hell.ihcore.Characters.System

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.imperial_hell.ihcore.Ihcore
import org.imperial_hell.ihcore.Model.Character

class UserManager(val server: Ihcore) {

    fun syncPlayer(player: ServerPlayerEntity, uuid: String) : Boolean {
        // Обработка пакета на сервере
        println("Синхронизация для $uuid")
        if (server.userService.isUserExists(uuid) == true) {
            val character = server.userService.getSelectedCharacter(uuid) as Character
            server.playerManager.applyCharacter(player, character)
            server.userService.updatePlayerForUser(uuid, player.name.string)
            server.playerDataStorage.updatePlayerDataAttribute(player.uuid) { currentData ->
                currentData.copy(characterUuid = uuid)
            }

            player.sendMessage(Text.of("Успешно синхронизировано"), false) // Отправляем сообщение игроку
            return true
        } else {
            return false
        }

    }

}