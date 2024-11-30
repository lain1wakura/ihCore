/*
package org.imperial_hell.ihSystems

import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text
import net.minecraft.text.Text.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TextColor
import java.text.ListFormat.Style

object IhLogger() {

    var debug: Boolean = true // Флаг для включения/выключения логирования

    // Метод для отправки информационного сообщения
    fun info(message: String) {
        if (debug) {
            // Преобразуем строку с кодами цвета в цветной текст
            val coloredMessage = message.replace('&', '§')  // Для совместимости с кодами цвета Spigot

            // Создаем объект Text
            val component = literal(coloredMessage).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)))

            // Отправляем сообщение в консоль
            sendToConsole(component)
        }
    }

    // Метод для отправки предупреждающего сообщения
    fun warn(message: String) {
        if (debug) {
            // Строим компонент с желтым цветом
            val component = literal(message).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF00)))

            // Отправляем сообщение в консоль
            sendToConsole(component)
        }
    }

    // Метод для отправки сообщения об ошибке
    fun error(message: String) {
        if (debug) {
            // Строим компонент с красным цветом
            val component = literal(message).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)))

            // Отправляем сообщение в консоль
            sendToConsole(component)
        }
    }

    // Метод для отправки сообщения об успехе
    fun success(message: String) {
        if (debug) {
            // Строим компонент с зеленым цветом
            val component = literal(message).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)))

            // Отправляем сообщение в консоль
            sendToConsole(component)
        }
    }
}
*/
