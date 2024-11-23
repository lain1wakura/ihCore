package org.imperial_hell.ihcore.Characters.System
import net.minecraft.server.network.ServerPlayerEntity
import org.imperial_hell.ihcore.Characters.Character

import org.imperial_hell.ihcore.Ihcore

class PlayerManager(private val server: Ihcore) {
    private val playerCache: MutableMap<String, Character> = mutableMapOf() // Используем UUID игрока как ключ



    fun addPlayer(character: Character?, uuid: String) {
        if (character != null) {
            playerCache[uuid] = character
            println("Игрок с UUID $uuid добавлен в кэш. Размер кэша: ${playerCache.size}.")
        }
    }

    fun createCharacterFromDatabase(uuid: String): Character? {
        val playerData = server.databaseManager.getAllFromTable(
            "characters", "WHERE uuid = ?", listOf(uuid)
        ).firstOrNull() ?: return null  // Получаем первую строку результата или null

        val jsonPath : String = playerData["json_path"].toString()
        val jsonString : String = server.jsonReader.readJsonFromFile("characters/$jsonPath/appearance.json").toString()
        val appearance = server.jsonReader.deserializeAppearance(jsonString)

        println("Создан объект Character: $playerData")

        // Создаем объект Player на основе данных
        return Character(
            uuid = uuid,
            name = playerData["name"] as? String ?: "",
            playerName = playerData["player"] as? String ?: "",
            nicknameColor = playerData["nickname_color"] as? String,
            description = playerData["desc"] as? String,
            jsonPath = playerData["json_path"] as? String ?: "",
            syncedPlayer = playerData["synced_player"] as? String,
            appearance = appearance
        )
    }

    fun applyCharacter(player: ServerPlayerEntity, character: Character) {
        println("Применение персонажа ${character.name}")
        server.playerManager.addPlayer(character,
            server.databaseManager.getProfileByNickname(player.name.string)[0].toString()
        )
        server.databaseManager.updateData("UPDATE minecraft_users SET character_name = ? WHERE nickname = ?", listOf(character.uuid, player.name.string))
        //MessageManager.send(player, "Применён персонаж &#${character.nicknameColor}${character.name}&7. Выбранный образ: &#${character.nicknameColor}${character.appearance.getCurrentStyleString()}", MessageManager.MessageType.INFO)
        val commandSource = player.commandSource
        val nickname = PlayerNameManager.createColoredText(character.name,"#${character.nicknameColor}")
        PlayerNameManager.updatePlayerName(player, nickname, PlayerNameManager.NameType.NICKNAME)
        player.server.commandManager.executeWithPrefix(commandSource, "skin url \"${character.appearance.styles[character.appearance.style].skinUrl}\" ${character.appearance.skinModel}")
    }

    // Метод для получения игрока из кэша или загрузки из БД при необходимости
    fun getPlayer(uuid: String): Character? {
        println("Запрос на игрока из кэша: $uuid. Возвращается ${playerCache[uuid]}")
        return playerCache[uuid] ?: createCharacterFromDatabase(uuid)
    }

    // Удаление игрока из кэша (например, при выходе)
    fun removePlayer(uuid: String) {
        playerCache.remove(uuid)
        println("Игрок с UUID $uuid был удален из кэша.")
    }

    // Сохранение всех данных игроков в базе перед завершением работы сервера
    fun saveAllPlayers() {
        for (player in playerCache.values) {
            server.databaseManager.updatePlayerProfile(player)
        }
    }

}
