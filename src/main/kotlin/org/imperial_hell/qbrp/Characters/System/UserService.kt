package org.imperial_hell.qbrp.Characters.System

import org.bson.Document
import org.imperial_hell.qbrp.System.Files.DatabaseManager
import org.imperial_hell.common.Characters.Character
import org.imperial_hell.qbrp.System.Files.MongoConverter

class UserService(private val databaseManager: DatabaseManager) {

    fun isUserExists(mark: String, mode: String = "uuid"): Boolean {
        return try {
            // Используем fetchOne из DatabaseManager
            val result = databaseManager.fetchOne(
                "profiles", // Название коллекции
                mapOf(mode to mark) // Условие поиска
            )
            result != null // Если результат не `null`, пользователь существует
        } catch (e: Exception) {
            println("Ошибка при проверке существования пользователя: ${e.message}")
            false
        }
    }

    fun updatePlayerForUser(userUuid: String, player: String): Boolean {
        return try {
            // Создаем фильтр для поиска пользователя по UUID
            val userFilter = mapOf("uuid" to userUuid)

            // Обновление поля "player" для пользователя
            val updateResult = databaseManager.updateFieldInDocument(
                "profiles",  // Коллекция пользователей
                userFilter,  // Фильтр для поиска пользователя
                "player",  // Поле, которое нужно обновить
                player  // Новое значение для поля "player"
            )

            if (updateResult) {
                println("Поле 'player' для пользователя $userUuid успешно обновлено.")
                return true
            } else {
                println("Ошибка при обновлении поля 'player' для пользователя $userUuid.")
                return false
            }
        } catch (e: Exception) {
            println("Ошибка при обновлении поля 'player' для пользователя $userUuid: ${e.message}")
            false
        }
    }


    fun getUserUuid(nickname: String): String? {
        return try {
            databaseManager.fetchOne(
                "profiles", // Название коллекции
                mapOf("nickname" to nickname) // Условие поиска
            )?.getString("uuid")
        } catch (e: Exception) {
            "Undefined"
        }.toString()
    }

    fun saveOrUpdateCharacterForUser(userUuid: String, character: Character): Boolean {
        return try {
            // Преобразуем объект Character в MongoDB документ
            val characterDocument = MongoConverter.toDocument(character)

            // Создаем фильтр для поиска пользователя по UUID
            val userFilter = mapOf("uuid" to userUuid)

            // Получаем профиль пользователя
            val userProfile = databaseManager.fetchOne("profiles", userFilter)

            if (userProfile != null) {
                // Получаем текущий список персонажей
                val characters = userProfile.getList("characters", Document::class.java)

                // Проверяем, есть ли уже персонаж с таким UUID
                val existingCharacterIndex = characters.indexOfFirst {
                    val charDoc = it as Document
                    charDoc.getString("uuid") == character.uuid
                }

                if (existingCharacterIndex != -1) {
                    // Если персонаж существует, обновляем его
                    characters[existingCharacterIndex] = characterDocument
                    println("Персонаж с UUID ${character.uuid} обновлен.")
                } else {
                    // Если персонажа нет, добавляем нового
                    characters.add(characterDocument)
                    println("Персонаж с UUID ${character.uuid} добавлен.")
                }

                // Обновляем поле "characters" для пользователя в базе
                val updateResult = databaseManager.updateFieldInDocument(
                    "profiles",  // Коллекция пользователей
                    userFilter,  // Фильтр для поиска пользователя
                    "characters",  // Поле, которое нужно обновить
                    characters  // Новый список персонажей
                )

                if (updateResult) {
                    println("Персонаж для пользователя $userUuid успешно сохранен или обновлен.")
                    return true
                } else {
                    println("Ошибка при сохранении или обновлении персонажа для пользователя $userUuid.")
                    return false
                }
            } else {
                println("Профиль с UUID $userUuid не найден.")
                return false
            }
        } catch (e: Exception) {
            println("Ошибка при сохранении или обновлении персонажа для пользователя $userUuid: ${e.message}")
            false
        }
    }


    fun getSelectedCharacter(uuid: String): Character? {
        return try {
            // Получаем профиль пользователя по UUID
            val profile = databaseManager.fetchOne(
                "profiles", // Название коллекции
                mapOf("uuid" to uuid) // Условие поиска
            )

            if (profile != null) {
                // Получаем массив `characters` и индекс выбранного персонажа
                val characters = profile.getList("characters", Document::class.java)
                val selectedIndex = profile.getInteger("character", -1)

                // Проверяем, что индекс корректный и список персонажей не пуст
                if (selectedIndex in characters.indices) {
                    val characterDoc = characters[selectedIndex] as Document

                    // Преобразуем данные из документа в объект Character
                    val character = MongoConverter.convert<Character>(characterDoc, Character::class)

                    character
                } else {
                    println("Персонаж не найден или индекс выходит за пределы массива.")
                    null
                }
            } else {
                println("Профиль с UUID $uuid не найден.")
                null
            }
        } catch (e: Exception) {
            println("Ошибка при получении выбранного персонажа: ${e.message}")
            null
        }
    }

}
