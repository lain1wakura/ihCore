package org.imperial_hell.ihcore.Files

import net.minecraft.server.network.ServerPlayerEntity
import org.apache.logging.log4j.LogManager
import org.imperial_hell.ihcore.Ihcore
import org.imperial_hell.ihcore.Characters.Character
import java.io.File
import java.sql.*

class DatabaseManager(private val server: Ihcore) {

    private var connection: Connection? = null
    private val logger = LogManager.getLogger("ih_core")

    fun isPathAccessible(path: String): Boolean {
        val file = File(path)

        // Проверка, существует ли файл или каталог
        if (!file.exists()) {
            logger.warn("Путь не существует: $path")
            return false
        }

        // Проверка прав доступа
        if (!file.canRead()) {
            logger.warn("Нет прав на чтение для пути: $path")
            return false
        }

        if (!file.canWrite()) {
            logger.warn("Нет прав на запись для пути: $path")
            return false
        }

        return true
    }

    // Устанавливаем подключение к базе данных
    fun connect() {
        try {
            // Получаем путь к базе данных из конфигурации или используем дефолтный путь
            val dbPath: String = IhConfig.absolutePath + "/main.db"

            // Проверка доступности пути
            if (isPathAccessible(dbPath)) {
                logger.info("Путь доступен для чтения и записи: $dbPath")
            } else {
                logger.error("Путь недоступен: $dbPath")
            }

            // Создаем подключение с использованием пути из конфигурации
            val url = "jdbc:sqlite:$dbPath"
            connection = DriverManager.getConnection(url)
            logger.info("Успешно подключено к базе данных.")
        } catch (e: SQLException) {
            logger.error("Ошибка подключения к базе данных: ${e.message}")
        }
    }

    // Закрываем соединение
    fun disconnect() {
        try {
            connection?.close()
            logger.info("Соединение с базой данных закрыто.")
        } catch (e: SQLException) {
            logger.error("Ошибка при закрытии соединения с базой данных: ${e.message}")
        }
    }

    // Функция перезагрузки соединения с базой данных
    fun reload() {
        // Пытаемся безопасно закрыть текущее соединение
        try {
            disconnect() // Безопасное закрытие текущего соединения
        } catch (e: Exception) {
            logger.error("Ошибка при попытке закрыть текущее соединение перед перезагрузкой: ${e.message}")
        }

        // Пытаемся установить новое соединение
        try {
            connect() // Установка нового соединения
            logger.info("Соединение с базой данных успешно перезагружено.")
        } catch (e: Exception) {
            logger.error("Ошибка при перезагрузке соединения с базой данных: ${e.message}")
        }
    }

    // Пример выполнения SQL-запроса
    fun executeQuery(query: String) {
        try {
            connection?.createStatement()?.executeUpdate(query)
        } catch (e: SQLException) {
            logger.error("Ошибка выполнения запроса: ${e.message}")
        }
    }

    // Получение результата запроса (например, для SELECT)
    fun executeQueryAndGetResult(query: String): ResultSet? {
        return try {
            connection?.createStatement()?.executeQuery(query)
        } catch (e: SQLException) {
            logger.error("Ошибка выполнения запроса: ${e.message}")
            null
        }
    }

    fun isUuidInDatabase(uuid: String): Boolean {
        reload()
        val query = "SELECT uuid FROM minecraft_users WHERE uuid = ?"
        val preparedStatement: PreparedStatement? = connection?.prepareStatement(query)

        if (preparedStatement == null) {
            logger.error("Не удалось создать подготовленное выражение, так как соединение с базой данных отсутствует.")
            return false
        }

        return try {
            preparedStatement.setString(1, uuid)  // Устанавливаем параметр в запрос
            val result: ResultSet = preparedStatement.executeQuery()
            result.next() // Если хотя бы одна строка есть, значит UUID найден
        } catch (e: SQLException) {
            logger.error("Ошибка при выполнении запроса: ${e.message}")
            false
        }
    }

    fun isPlayerNicknameInDatabase(nickname: String): Boolean {
        val query = "SELECT nickname FROM minecraft_users WHERE uuid = ?"
        val preparedStatement: PreparedStatement? = connection?.prepareStatement(query)

        if (preparedStatement == null) {
            logger.error("Не удалось создать подготовленное выражение, так как соединение с базой данных отсутствует.")
            return false
        }

        return try {
            preparedStatement.setString(1, nickname)  // Устанавливаем параметр в запрос
            val result: ResultSet = preparedStatement.executeQuery()
            result.next() // Если хотя бы одна строка есть, значит UUID найден
        } catch (e: SQLException) {
            logger.error("Ошибка при выполнении запроса: ${e.message}")
            false
        }
    }

    // Пример получения данных для игрока
    fun getProfileByNickname(nickname: String): List<String> {
        val query = "SELECT uuid, discord_id, character_name FROM minecraft_users WHERE nickname = ?"
        var uuid: String = ""
        var discordId: String = ""
        var character: String = ""

        try {
            val preparedStatement: PreparedStatement? = connection?.prepareStatement(query)
            preparedStatement?.setString(1, nickname)  // Подставляем значение uuid в запрос

            val resultSet = preparedStatement?.executeQuery()
            if (resultSet != null && resultSet.next()) {
                uuid = resultSet.getString("uuid")
                discordId = resultSet.getString("discord_id")
                character = resultSet.getString("character_name")
            // Получаем значение discord_id из результата
            }

        } catch (e: SQLException) {
            logger.error("Ошибка при получении discord_id: ${e.message}")
        }

        return listOf(uuid, discordId, character, nickname)
    }

    fun getProfileByUuid(uuid: String): List<String> {
        val query = "SELECT nickname, discord_id, character_name FROM minecraft_users WHERE uuid = ?"
        var discordId: String = ""
        var nickname: String = ""
        var character: String = ""

        try {
            val preparedStatement: PreparedStatement? = connection?.prepareStatement(query)
            preparedStatement?.setString(1, uuid)  // Подставляем значение uuid в запрос

            val resultSet = preparedStatement?.executeQuery()
            if (resultSet != null && resultSet.next()) {
                discordId = resultSet.getString("discord_id")
                nickname = resultSet.getString("nickname")
                character = resultSet.getString("character_name")
                // Получаем значение discord_id из результата
            }

        } catch (e: SQLException) {
            logger.error("Ошибка при получении discord_id: ${e.message}")
        }

        return listOf(uuid, discordId, character, nickname)
    }

    // Получение всех персонажей игрока
    fun getAllPlayerCharacters(uuid: String) : List<Map<String, Any>> {
        val discordId = getProfileByUuid(uuid)[1]
        println("discordId: $discordId")
        val results = getAllFromTable("characters", "WHERE player = ?", listOf(discordId.toString()))

        return if (results.isEmpty()) {
            logger.info("Персонажи не найдены.")
            listOf()
        } else {
            results
        }
    }

    // Универсальная функция для получения всех строк из таблицы с возможным WHERE
    fun getAllFromTable(tableName: String, whereClause: String = "", params: List<Any> = listOf()): List<Map<String, Any>> {
        val query = "SELECT * FROM $tableName $whereClause"
        val resultList = mutableListOf<Map<String, Any>>()

        val preparedStatement: PreparedStatement? = connection?.prepareStatement(query)

        if (preparedStatement == null) {
            logger.error("Не удалось создать подготовленное выражение, так как соединение с базой данных отсутствует.")
            return resultList
        }

        try {
            // Устанавливаем параметры, если они переданы
            for (i in params.indices) {
                preparedStatement.setObject(i + 1, params[i])
            }

            val resultSet: ResultSet = preparedStatement.executeQuery()
            val metaData = resultSet.metaData
            val columnCount = metaData.columnCount

            // Читаем все строки результата
            while (resultSet.next()) {
                val row = mutableMapOf<String, Any>()
                for (i in 1..columnCount) {
                    val columnName = metaData.getColumnName(i)
                    val columnValue = resultSet.getObject(i)
                    row[columnName] = columnValue ?: "NULL"
                }
                resultList.add(row)
            }

        } catch (e: SQLException) {
            logger.error("Ошибка при выполнении запроса: ${e.message}")
        }

        return resultList
    }

    fun updatePlayerProfile(player: Character): Boolean {
        val query = """
        UPDATE characters 
        SET name = ?, player = ?, nickname_color = ?, desc = ?, json_path = ?, synced_player = ?
        WHERE uuid = ?
    """

        val preparedStatement: PreparedStatement? = connection?.prepareStatement(query)

        // Проверка, что соединение и подготовленное выражение не равны null
        if (preparedStatement == null) {
            println("Не удалось создать подготовленное выражение, так как соединение с базой данных отсутствует.")
            return false
        }

        return try {
            // Заполняем параметры для SQL-запроса
            preparedStatement.setString(1, player.name)
            preparedStatement.setString(2, player.playerName)
            preparedStatement.setString(3, player.nicknameColor)
            preparedStatement.setString(4, player.description)
            preparedStatement.setString(5, player.jsonPath)
            preparedStatement.setString(6, player.syncedPlayer)
            preparedStatement.setString(7, player.uuid)

            // Выполняем запрос на обновление
            val affectedRows = preparedStatement.executeUpdate()

            if (affectedRows > 0) {
                println("Профиль игрока с UUID ${player.uuid} успешно обновлен в базе данных.")
                true
            } else {
                println("Не удалось обновить профиль игрока с UUID ${player.uuid} — запись не найдена.")
                false
            }

        } catch (e: SQLException) {
            println("Ошибка при обновлении профиля игрока: ${e.message}")
            false
        } finally {
            preparedStatement.close()
        }
    }

    // Функция для обновления данных в таблице
    fun updateData(query: String, vararg params: Any): Boolean {
        val preparedStatement: PreparedStatement? = connection?.prepareStatement(query)

        if (preparedStatement == null) {
            logger.error("Не удалось создать подготовленное выражение, так как соединение с базой данных отсутствует.")
            return false
        }

        return try {
            // Устанавливаем параметры запроса
            for (i in params.indices) {
                preparedStatement.setObject(i + 1, params[i])
            }

            // Выполняем запрос на обновление данных
            val affectedRows = preparedStatement.executeUpdate()

            if (affectedRows > 0) {
                logger.info("Данные успешно обновлены.")
                true
            } else {
                logger.warn("Не удалось обновить данные.")
                false
            }

        } catch (e: SQLException) {
            logger.error("Ошибка при обновлении данных: ${e.message}")
            false
        }
    }
}
