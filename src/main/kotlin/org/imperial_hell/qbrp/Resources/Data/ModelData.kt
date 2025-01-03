package org.imperial_hell.qbrp.Resources.Data
import com.google.gson.JsonObject

class ModelData(val json: JsonObject) : RawData() {
    val textures: MutableMap<String, String> = mutableMapOf()

    init {
        // Инициализируем textures из исходного JSON
        val texturesJson = json.getAsJsonObject("textures")
        texturesJson?.entrySet()?.forEach { entry ->
            textures[entry.key] = entry.value.asString
        }
    }

    // Обновляем JSON при изменении textures
    private fun updateJson() {
        val texturesJson = JsonObject()
        textures.forEach { (key, value) ->
            texturesJson.addProperty(key, value)
        }
        json.add("textures", texturesJson)
    }

    // Метод для добавления текстуры
    fun addTexture(key: String, value: String) {
        textures[key] = value
        updateJson()
    }

    // Метод для удаления текстуры
    fun removeTexture(key: String) {
        textures.remove(key)
        updateJson()
    }

    // Новый метод для обработки textures.values
    fun processTextures(newTextures: List<String>) {
        // Применяем новый список путей к всем значениям в textures
        textures.keys.zip(newTextures).forEach { (key, newTexturePath) ->
            textures[key] = newTexturePath  // Обновляем каждый путь текстуры
        }
        updateJson()  // Обновляем JSON после изменения
    }
}
