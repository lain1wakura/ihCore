package org.imperial_hell.qbrp.Sync
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.ihSystems.IhLogger.MessageType
import org.imperial_hell.qbrp.Files.IhConfig
import org.imperial_hell.qbrp.System.ConsoleColors.bold
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.writeText

object ResourcePackBaker {

    private const val RESOURCE_PACK_NAME = "qbrp"  // Имя ресурспака
    private const val PACK_DESCRIPTION = "Ресурс-пак предметов"  // Описание ресурспака
    private const val PACK_FORMAT = 15  // Версия ресурспака

    var models_count = 0

    fun createResourcePackStructure(baseDirectory: File) {
        try {

            val base = baseDirectory.apply {
                mkdirs()  // Создаём папку моделей
            }

            // assets/qbrp/models
            val modelsDir = File(baseDirectory, "models/item").apply {
                mkdirs()  // Создаём папку моделей
            }

            // assets/qbrp/models/item/custom_item_generated.json
            val jsonGenerated = File(baseDirectory, "models/item/custom_item_generated.json").apply {
                parentFile.mkdirs() // Создаём папки, если их ещё нет
                writeText(
                    """
            {
              "parent": "item/generated",
              "textures": {
                "layer0": "qbrp:item/custom_item_generated"
              },
              "overrides": []
            }
            """.trimIndent()
                )
            }

            // assets/qbrp/models/item/custom_item_handled.json
            val jsonHandled = File(baseDirectory, "models/item/custom_item_handheld.json").apply {
                parentFile.mkdirs() // Создаём папки, если их ещё нет
                writeText(
                    """
            {
              "parent": "item/handheld",
              "textures": {
                "layer0": "qbrp:item/custom_item_handheld"
              },
              "overrides": []
            }
            """.trimIndent()
                )
            }

            // assets/qbrp/textures
            val texturesDir = File(baseDirectory, "textures/item").apply {
                mkdirs()  // Создаём папку текстур
            }

            // assets/qbrp/sounds
            val soundsDir = File(baseDirectory, "sounds").apply {
                mkdirs()  // Создаём папку звуков
            }

            // Создаём pack.mcmeta файл
            val packMetaFile = File(IhConfig.SERVER_PACK_PATH.toFile(), "pack.mcmeta").apply {
                writeText("""
                    {
                        "pack": {
                            "pack_format": $PACK_FORMAT,
                            "description": "$PACK_DESCRIPTION"
                        }
                    }
                """.trimIndent())
            }
        } catch (e: Exception) {
            IhLogger.log("[!] ${e.message}", type = MessageType.ERROR)
        }
    }

    fun scanExtensions(directory: File, extensions: List<String>): List<File> {
        val matchingFiles = mutableListOf<File>()

        extensions.forEach { extension ->
            matchingFiles.addAll(scanExtension(directory, extension))
        }

        return matchingFiles
    }

    fun scanExtension(directory: File, extension: String): List<File> {
        val matchingFiles = mutableListOf<File>()

        if (!directory.exists() || !directory.isDirectory) {
            IhLogger.log("Указанная директория не существует или не является папкой: ${directory.absolutePath}", IhLogger.MessageType.ERROR)
            return matchingFiles
        }

        directory.walkTopDown().forEach { file ->
            if (file.isFile && file.extension.equals(extension, ignoreCase = true)) {
                matchingFiles.add(file)
            }
        }

        return matchingFiles
    }

    fun copyFile(sourceFile: File, destinationFile: File) {
        try {
            if (!sourceFile.exists()) {
                IhLogger.log("[!] Исходный файл не найден: ${sourceFile.path}", IhLogger.MessageType.WARN)
                return
            }

            destinationFile.parentFile?.mkdirs()
            sourceFile.inputStream().use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            IhLogger.log("<<[>]>> ${sourceFile.path.replace("qbrpres", "")} -> ${destinationFile.path.replace("qbrpres", "")}", IhLogger.MessageType.INFO)
        } catch (e: Exception) {
            IhLogger.log("[!] Ошибка копирования: ${sourceFile.path.replace("qbrpres", "")} -> ${destinationFile.path.replace("qbrpres", "")} | ${e.message}", IhLogger.MessageType.ERROR)
        }
    }

    fun processJsonFiles(jsonFiles: List<File>, targetDirectory: File, packPath: File) {
        var customModelData = 1 // Счетчик для custom_model_data

        jsonFiles.forEach { file ->
            try {
                try {
                    // Шаг 1: Чтение JSON-файла
                    val jsonContent = file.readText()
                    val jsonElement = Json.parseToJsonElement(jsonContent).jsonObject

                    // Шаг 2: Копирование JSON модели в новое расположение
                    val modelCopyDestinationPath = File(packPath, "models/item/${file.name}")
                    copyFile(file, modelCopyDestinationPath)

                    // Шаг 3: Обновление текстур в модели на новом месте
                    val textures = jsonElement["textures"]?.jsonObject.orEmpty()
                    var modifiedTexturePath = ""
                    val updatedTextures = textures.mapValues { (key, texturePathJson) ->
                        val texturePathString = texturePathJson.jsonPrimitive.content
                        if (texturePathString.split("/")[0] != "qbrp:item") {
                            modifiedTexturePath = "qbrp:item/$texturePathString" // Добавляем префикс

                            // Копируем текстуры
                            val sourceFile = File(targetDirectory, "$texturePathString.png")
                            val destinationFile = File(packPath, "textures/item/$texturePathString.png")
                            if (sourceFile.exists()) {
                                copyFile(sourceFile, destinationFile)
                            } else {
                                IhLogger.log("[!] Текстура не найдена: ${sourceFile.path}", IhLogger.MessageType.ERROR)
                            }
                        } else {
                            modifiedTexturePath = texturePathString
                        }

                        // Возвращаем обновленный путь
                        JsonPrimitive(modifiedTexturePath)
                    }

                    val updatedJsonElement = jsonElement.toMutableMap()
                    updatedJsonElement["textures"] = JsonObject(updatedTextures)

                    // Обновляем JSON-объект
                    // Шаг 3: Копирование JSON модели
                    val destinationPath = "models/item/" + file.name
                    copyFile(file, File(packPath, destinationPath))
                    modelCopyDestinationPath.writeText(Json.encodeToString(JsonObject.serializer(), JsonObject(updatedJsonElement)))
                } catch (e: Exception) {
                    IhLogger.log("[!] Ошибка обработки JSON файла: ${file.path} | ${e.message}", IhLogger.MessageType.ERROR)
                }
                val modelName = file.nameWithoutExtension

                // Шаг 4: Добавление в соответствующие файлы overrides и обновление itemres.json
                val overrideFileName = if (file.name.endsWith("_handheld.json")) "custom_item_handheld.json" else "custom_item_generated.json"
                val overrideFile = File(packPath, "models/item/$overrideFileName")

                addOverrideToFile(overrideFile, customModelData++, modelName)
                updateModelDataFile(File(IhConfig.SERVER_RESOURCES_PATH.toFile(), "itemres.json"), modelName, customModelData)
            } catch (e: Exception) {
                IhLogger.log("[!] Ошибка обработки JSON файла: ${file.path} | ${e.message}", IhLogger.MessageType.ERROR)
            }
            models_count++
        }
    }

    fun updateModelDataFile(overridesFile: File, path: String, customModelData: Int) {
        // Шаг 1: Загрузка текущего содержимого файла
        val currentData = if (overridesFile.exists()) {
            try {
                Json.decodeFromString<List<JsonObject>>(overridesFile.readText())
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        // Шаг 2: Создание новой записи
        val newEntry = buildJsonObject {
            put("path", JsonPrimitive(path))
            put("custom_model_data", JsonPrimitive(customModelData))
        }

        val updatedData: List<JsonObject> = currentData + newEntry
        overridesFile.writeText(Json.encodeToString(ListSerializer(JsonObject.serializer()), updatedData))

    }

    fun zipFolder(sourceFolder: File, outputZip: File) {
        if (!sourceFolder.exists() || !sourceFolder.isDirectory) {
            throw IllegalArgumentException("Источник должен быть существующей директорией: ${sourceFolder.absolutePath}")
        }

        ZipOutputStream(FileOutputStream(outputZip)).use { zipOut ->
            sourceFolder.walkTopDown().forEach { file ->
                val relativePath = file.relativeTo(sourceFolder).path
                val zipEntry = ZipEntry(
                    if (file.isDirectory) "$relativePath/" else relativePath
                )

                zipOut.putNextEntry(zipEntry)
                if (file.isFile) {
                    file.inputStream().use { input ->
                        input.copyTo(zipOut)
                    }
                }
                zipOut.closeEntry()
            }
        }
        IhLogger.log("Архив ${outputZip.name} создан успешно.")
    }


    fun addOverrideToFile(file: File, customModelData: Int, modelPath: String) {
        // Шаг 1: Прочитать существующий JSON из файла, если он есть
        val jsonObject = if (file.exists()) {
            val jsonContent = file.readText()
            Json.parseToJsonElement(jsonContent).jsonObject.toMutableMap()
        } else {
            mutableMapOf<String, JsonElement>("overrides" to JsonArray(emptyList()))
        }

        // Шаг 2: Получить массив `overrides` или создать его
        val overrides = (jsonObject["overrides"] as? JsonArray)?.toMutableList()
            ?: mutableListOf<JsonElement>()

        // Шаг 3: Добавить новый элемент в `overrides`
        val newOverride = buildJsonObject {
            put("predicate", buildJsonObject {
                put("custom_model_data", JsonPrimitive(customModelData)) // Оборачиваем в JsonPrimitive
            })
            put("model", JsonPrimitive("qbrp:item/$modelPath")) // Оборачиваем в JsonPrimitive
        }
        overrides.add(newOverride)

        // Обновляем `overrides` в основном объекте
        jsonObject["overrides"] = JsonArray(overrides)

        // Шаг 4: Записать обновлённый JSON обратно в файл
        val updatedJson = JsonObject(jsonObject)
        file.writeText(Json.encodeToString(JsonObject.serializer(), updatedJson))
    }

    fun listFilesInDirectory(directoryPath: String): List<File> {
        val directory = File(directoryPath)

        // Проверяем, что путь указывает на существующую директорию
        if (!directory.exists() || !directory.isDirectory) {
            println("Указанный путь не существует или не является директорией: $directoryPath")
            return emptyList()
        }

        // Получаем список файлов
        return directory.listFiles()?.toList() ?: emptyList()
    }

    fun process(packDirectory: java.nio.file.Path, resourcesDirectory: java.nio.file.Path): String {
        IhLogger.log(bold("<<Инициализация пакета ресурсов>>"))
        IhLogger.log("<<|>> Директория ресурсов: ${packDirectory.pathString}")
        IhLogger.log("<<|>> Выходные данные: ${resourcesDirectory.pathString}")
        try {
            if (packDirectory.exists()) {
                Files.walk(packDirectory)
                    .sorted(Comparator.reverseOrder())
                    .map { it.toFile() }
                    .forEach { it.delete() }
            }
            IhLogger.log("<<[!]>> Инициализации структуры пакета ресурсов")
            createResourcePackStructure(packDirectory.toFile())
            IhLogger.log("<<[!]>> Обработка моделей")
            processJsonFiles(
                scanExtension(resourcesDirectory.toFile(), "json"),
                resourcesDirectory.toFile(),
                packDirectory.toFile()
            )
            IhLogger.log("<<[!]>> Архивация")
            zipFolder(IhConfig.SERVER_BAKED_PATH.toFile(), File(IhConfig.DISTRIBUTION_PACK_PATH.toString()))
            IhLogger.log(bold("<<Пакет ресурсов создан>>"))
        } catch (e: Exception) {
            IhLogger.log("[!] $e", type = IhLogger.MessageType.ERROR)
            return e.message.toString()
        }
        return ""
    }

}