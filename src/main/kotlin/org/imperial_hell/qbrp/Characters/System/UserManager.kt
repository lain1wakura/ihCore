package org.imperial_hell.qbrp.Characters.System

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.imperial_hell.common.Characters.Character
import org.imperial_hell.qbrp.Sync.PlayerDataManager

class UserManager(
    val dbService: UserService,
    val characterManager: CharacterManager,
    val playerDataManager: PlayerDataManager) {

    fun syncPlayer(player: ServerPlayerEntity, uuid: String) : Boolean {
        // Обработка пакета на сервере
        if (dbService.isUserExists(uuid) == true) {
            val character = dbService.getSelectedCharacter(uuid) as Character
            characterManager.applyCharacter(player, character)
            dbService.updatePlayerForUser(uuid, player.name.string)
            playerDataManager.updatePlayerDataAttribute(player.uuid) { currentData ->
                currentData.copy(characterUuid = uuid)
            }

            player.sendMessage(Text.of("Успешно синхронизировано"), false) // Отправляем сообщение игроку
            return true
        } else {
            return false
        }

    }

}