package org.imperial_hell.qbrp.Characters.System
import net.minecraft.server.network.ServerPlayerEntity
import org.imperial_hell.common.Characters.Character

import org.imperial_hell.qbrp.Removed.PlayerNameManager

class CharacterManager(val userService: UserService,) {
    private val characterCache: MutableMap<String, Character> = mutableMapOf() // Используем UUID игрока как ключ

    fun addCharacter(character: Character?) {
        if (character != null) {
            val uuid = character.uuid
            characterCache[uuid] = character
            println("Игрок с UUID $uuid добавлен в кэш. Размер кэша: ${characterCache.size}.")
        }
    }

    fun applyCharacter(player: ServerPlayerEntity, character: Character) {
        println("Применение персонажа ${character.name}")
        addCharacter(character)
        //MessageManager.send(player, "Применён персонаж &#${character.nicknameColor}${character.name}&7. Выбранный образ: &#${character.nicknameColor}${character.appearance.getCurrentStyleString()}", MessageManager.MessageType.INFO)
        val commandSource = player.commandSource
        val nickname = PlayerNameManager.createColoredText(character.name,"#${character.nicknameColor}")
        PlayerNameManager.updatePlayerName(player, nickname, PlayerNameManager.NameType.NICKNAME)
        //player.server.commandManager.executeWithPrefix(commandSource, "skin url \"${character.appearance.styles[character.appearance.style].skinUrl}\" ${character.appearance.skinModel}")
        player.server.commandManager.executeWithPrefix(commandSource, "skin url \"${character.appearance.skinUrl}\" ${character.appearance.skinModel}")
    }

    // Удаление игрока из кэша (например, при выходе)
    fun removeCharacter(uuid: String) {
        characterCache.remove(uuid)
        println("Игрок с UUID $uuid был удален из кэша.")
    }

    // Получение всех персонажей из кэша
    fun getAllCharacters(): List<Character> {
        return characterCache.values.toList() // Возвращаем список всех персонажей
    }

    fun saveAllCharacters() {
        getAllCharacters().forEach { character -> userService.saveOrUpdateCharacterForUser(character.author, character) }
    }

}
