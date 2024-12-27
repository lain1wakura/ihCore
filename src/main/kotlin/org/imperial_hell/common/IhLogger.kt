package org.imperial_hell.ihSystems

import org.imperial_hell.common.Utils.ConsoleColors
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object IhLogger {
    private val logger: Logger = LoggerFactory.getLogger("IhLogger")
    var debug: Boolean = false // Флаг для включения/выключения логирования

    /**
     * Логирование сообщения с заданным типом.
     * @param message Сообщение для логирования.
     * @param type Тип сообщения (INFO, WARN, ERROR, SUCCESS).
     * @param debugMode Если true, сообщение будет логироваться только в режиме отладки.
     */
    fun log(message: String, type: MessageType = MessageType.INFO, debugMode: Boolean = false) {
        // Если debugMode активен и debug = false, не логируем сообщение
        if (debugMode && !debug) return

        // Форматируем текст: текст в << >> выделяется синим цветом
        val formattedMessage = formatMessage(message)

        // В зависимости от типа сообщения выбираем действие
        when (type) {
            MessageType.INFO -> logger.info(formattedMessage)
            MessageType.WARN -> logger.warn("${ConsoleColors.ORANGE}$formattedMessage")
            MessageType.ERROR -> logger.error(formattedMessage)
            MessageType.SUCCESS -> logger.info("${ConsoleColors.GREEN}$formattedMessage") // Для успеха используем INFO
        }
    }

    private fun formatMessage(message: String): String {

        val regex = Regex("""<<(.*?)>>""") // Регулярное выражение для поиска текста в (( ))
        return regex.replace(message) { matchResult ->
            "${ConsoleColors.BLUE}${matchResult.groupValues[1]}${ConsoleColors.RESET}" // Заменяем найденный текст на синий
        }
    }

    enum class MessageType {
        INFO, WARN, ERROR, SUCCESS
    }
}
