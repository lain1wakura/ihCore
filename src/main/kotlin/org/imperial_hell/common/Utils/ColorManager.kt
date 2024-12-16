package org.imperial_hell.ihSystems

import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object ColorManager {

    // Регулярное выражение для HEX-кодов (&#RRGGBB)
    private val hexPattern = Pattern.compile("&#[a-fA-F0-9]{6}")

    /**
     * Форматирует текст, заменяя символы цвета Minecraft и HEX-коды.
     *
     * @param text Текст, который нужно отформатировать
     * @return Текст с форматированными цветами для Minecraft
     */
    fun format(text: String): Text {
        // Преобразуем все HEX-коды (например, &#FF5733) в формат Minecraft
        val formattedText = applyHexColors(text)

        // Преобразуем символы цвета Minecraft (&c, &6 и т. д.)
        return translateAlternateColorCodes(formattedText)
    }

    /**
     * Применяет HEX-коды к тексту, преобразуя их в формат Minecraft.
     *
     * @param text Текст с HEX-кодами
     * @return Текст с цветами в Minecraft-формате
     */
    private fun applyHexColors(text: String): String {
        val matcher = hexPattern.matcher(text)
        val buffer = StringBuffer()

        while (matcher.find()) {
            // Получаем HEX-код, убираем `&` и заменяем на формат Minecraft
            val hexCode = matcher.group().substring(2) // убираем `&#`
            val minecraftHex = "§x§${hexCode[0]}§${hexCode[1]}§${hexCode[2]}§${hexCode[3]}§${hexCode[4]}§${hexCode[5]}"
            matcher.appendReplacement(buffer, minecraftHex)
        }
        matcher.appendTail(buffer)

        return buffer.toString()
    }

    /**
     * Преобразует стандартные символы цвета Minecraft (&c, &6 и т. д.) в формат Minecraft (§c, §6 и т. д.).
     *
     * @param text Текст с символами цвета
     * @return Текст с форматированными цветами
     */
    private fun translateAlternateColorCodes(text: String): Text {
        var formattedText = text.replace("&", "§")

        // Разбиваем текст на части по символу '§' и обрабатываем каждую
        val components = formattedText.split("§")
        val result = Text.literal("")

        for (component in components) {
            if (component.isEmpty()) continue
            val colorCode = component[0]
            val rest = component.substring(1)

            val color = Formatting.byCode(colorCode)
            if (color != null) {
                result.append(Text.literal(rest).styled { it.withColor(color) })
            } else {
                result.append(Text.literal("§$component"))
            }
        }

        return result
    }
}
