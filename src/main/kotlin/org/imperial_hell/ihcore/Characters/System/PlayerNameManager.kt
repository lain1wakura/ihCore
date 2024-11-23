package org.imperial_hell.ihcore.Characters.System
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.TextColor
import java.util.*

object PlayerNameManager {

    private val playerPrefixes: MutableMap<UUID, Text> = HashMap()
    private val playerSuffixes: MutableMap<UUID, Text> = HashMap()
    private val playerNicknames: MutableMap<UUID, Text> = HashMap()
    private val fullPlayerNames: MutableMap<UUID, Text> = HashMap()

    // Метод для обновления имени игрока (префикс, суффикс или никнейм)
    fun updatePlayerName(player: ServerPlayerEntity, name: Text, type: NameType) {
        when (type) {
            NameType.PREFIX -> playerPrefixes[player.uuid] = name
            NameType.SUFFIX -> playerSuffixes[player.uuid] = name
            NameType.NICKNAME -> playerNicknames[player.uuid] = name
        }
        updateFullPlayerName(player)
    }

    // Получение полного имени игрока с учетом префиксов и суффиксов
    fun getFullPlayerName(player: ServerPlayerEntity): Text {
        if (!fullPlayerNames.containsKey(player.uuid)) {
            updateFullPlayerName(player)
        }
        return fullPlayerNames[player.uuid]!!
    }

    // Обновление полного имени игрока с учетом префиксов, суффиксов и никнеймов
    private fun updateFullPlayerName(player: ServerPlayerEntity) {
        val prefix = playerPrefixes[player.uuid]
        val suffix = playerSuffixes[player.uuid]
        val nickname = playerNicknames[player.uuid]

        // Начинаем с пустого текста
        val name: MutableText = Text.literal("")

        // Добавляем префикс, если он есть
        if (prefix != null) {
            name.append(prefix)
            name.append(" ")
        }

        // Добавляем никнейм или стандартное имя игрока
        if (nickname != null) {
            name.append(nickname)
        } else {
            name.append(player.name)
        }

        // Добавляем суффикс, если он есть
        if (suffix != null) {
            name.append(" ")
            name.append(suffix)
        }

        // Сохраняем обновленное полное имя
        fullPlayerNames[player.uuid] = name
    }

    // Метод для создания текста с поддержкой HEX-цветов
    fun createColoredText(text: String, hexColor: String): Text {
        // Проверяем, является ли цвет корректным HEX-кодом (длина 7 символов, начинается с #)
        if (hexColor.startsWith("#") && hexColor.length == 7) {
            val color = TextColor.parse(hexColor)
            if (color != null) {
                return Text.literal(text).setStyle(Style.EMPTY.withColor(color))
            }
        }
        // Если цвет не корректный или не поддерживается, возвращаем обычный текст
        return Text.literal(text)
    }

    // Типы имен для обновления (префикс, суффикс, никнейм)
    enum class NameType {
        PREFIX,
        SUFFIX,
        NICKNAME
    }
}