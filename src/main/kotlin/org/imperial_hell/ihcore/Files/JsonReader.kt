
package org.imperial_hell.ihcore.Files

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.imperial_hell.ihcore.Characters.Character
import kotlinx.io.files.Path
import org.imperial_hell.ihcore.Characters.Character.Appearance
import org.imperial_hell.ihcore.Characters.Character.Appearance.SkinModel
import org.imperial_hell.ihcore.Characters.PlayerStyle
import org.imperial_hell.ihcore.Ihcore
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Paths
import java.util.*
import java.util.logging.Logger

class JsonReader(private val server: Ihcore) {

    val objectMapper = jacksonObjectMapper().registerKotlinModule()

    companion object {

        // Основной метод десериализации Appearance из JSON

        // Метод десериализации для PlayerStyle
        private fun deserializePlayerStyle(json: JsonNode): PlayerStyle {
            val name = json.path("name").asText("Unnamed Style")
            val desc = json.path("desc").asText(null)
            val descType = json.path("desc_type").asText("append")
            val openTime = json.path("open_time").asInt(0)
            val skinUrl = json.path("skin_url").asText(null)

            val top = json.path("top").map { it.asText() }.toMutableList()
            val bottom = json.path("bottom").map { it.asText() }.toMutableList()
            val fullCover = json.path("full_cover").map { it.asText() }.toMutableList()
            val footwear = json.path("footwear").map { it.asText() }.toMutableList()
            val accessories = json.path("accessories").map { it.asText() }.toMutableList()
            val headwear = json.path("headwear").map { it.asText() }.toMutableList()

            return PlayerStyle(
                name = name,
                desc = desc,
                descType = descType,
                openTime = openTime,
                top = top,
                bottom = bottom,
                fullCover = fullCover,
                footwear = footwear,
                accessories = accessories,
                headwear = headwear,
                skinUrl = skinUrl
            )
        }


        // Вспомогательная функция для преобразования JSONArray в список строк
        private fun toStringList(jsonArray: JSONArray): MutableList<String> {
            val list = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
            return list
        }
    }

    fun updateAppearanceJson(character: Character) {
        val appearance = character.appearance
        val path = "/characters/" + character.jsonPath + "/appearance.json"

        val jsonString = readJsonFromFile(path)?.toString() ?: return
        val jsonObject = objectMapper.readTree(jsonString).deepCopy()

        // Обновление стилей
        val stylesArrayNode = objectMapper.createArrayNode()
        for (style in appearance.styles) {
            val styleObjectNode = objectMapper.createObjectNode()
            styleObjectNode.put("py/object", "__main__.PlayerStyle")
            styleObjectNode.put("name", style.name)
            styleObjectNode.put("desc", style.desc ?: "Нет описания")
            styleObjectNode.put("desc_type", style.descType)
            styleObjectNode.put("open_time", style.openTime)
            styleObjectNode.putArray("top").addAll(style.top.map { objectMapper.convertValue(it) })
            styleObjectNode.putArray("bottom").addAll(style.bottom.map { objectMapper.convertValue(it) })
            styleObjectNode.putArray("full_cover").addAll(style.fullCover.map { objectMapper.convertValue(it) })
            styleObjectNode.putArray("footwear").addAll(style.footwear.map { objectMapper.convertValue(it) })
            styleObjectNode.putArray("accessories").addAll(style.accessories.map { objectMapper.convertValue(it) })
            styleObjectNode.putArray("headwear").addAll(style.headwear.map { objectMapper.convertValue(it) })
            styleObjectNode.put("skin_url", style.skinUrl)

            stylesArrayNode.add(styleObjectNode)
        }

        (jsonObject as ObjectNode).set("styles", stylesArrayNode)
        jsonObject.put("style", appearance.style)

        try {
            writeJsonToFile(jsonObject, path)
            println("Файл успешно обновлен.")
        } catch (e: Exception) {
            println("Ошибка при записи в файл: ${e.message}")
        }
    }


    // Метод для записи обновленного JSON обратно в файл
    fun writeJsonToFile(jsonObject: JsonNode, path: String) {
        try {
            val file = File("${IhConfig.absolutePath}/$path")

            // Создаем директории, если они не существуют
            file.parentFile?.mkdirs()

            // Записываем JSON в файл с отступами для читабельности
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, jsonObject)

            println("Файл был успешно сохранен по пути: ${file.absolutePath}")
        } catch (e: IOException) {
            println("Ошибка при записи JSON в файл: ${e.message}")
        }
    }


    fun deserializeAppearance(jsonString: String): Appearance {
        val json = objectMapper.readTree(jsonString)

        val model = json.path("model").asText()
        val description = json.path("desc").asText(null)
        val skinUrl = json.path("skin_url").asText("")
        val style = json.path("style").asInt(0)

        val skinModel = when (model.lowercase()) {
            "slim" -> SkinModel.SLIM
            else -> SkinModel.CLASSIC
        }

        val stylesJsonArray = json.path("styles")
        val styles = stylesJsonArray.map { deserializePlayerStyle(it) }.toMutableList()

        return Appearance(
            mode = model,
            description = description,
            skinModel = skinModel,
            styles = styles,
            skinUrl = skinUrl,
            style = style
        )
    }


    fun readJsonFromFile(filePath: String): JsonNode? {
        return try {
            val file = File("${IhConfig.absolutePath}/$filePath")
            if (file.exists()) {
                // Чтение JSON с использованием Jackson
                objectMapper.readTree(file)
            } else {
                println("Файл не найден по пути: $filePath")
                null
            }
        } catch (e: IOException) {
            println("Ошибка при чтении файла: ${e.message}")
            null
        }
    }
}

