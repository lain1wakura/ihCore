package org.imperial_hell.qbrp.System.Files

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.bson.Document
import org.imperial_hell.ihSystems.IhLogger
import kotlin.reflect.KClass

object MongoConverter {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Преобразует документ MongoDB в датакласс.
     */
    fun <T : Any> convert(document: Document, clazz: KClass<T>): T? {
        // Преобразование документа MongoDB в JSON-строку
        val json = document.toJson()

        // Преобразование JSON-строки в объект нужного типа
        try {
            return gson.fromJson(json, clazz.java)
        } catch (e: Exception) {
            IhLogger.log("Ошибка конвертации ${clazz.simpleName} в датакласс: $e")
        }
        return null
    }

    /**
     * Преобразует объект в документ MongoDB.
     */
    fun <T : Any> toDocument(obj: T): Document? {
        // Преобразование объекта в JSON-строку
        val json = gson.toJson(obj)
        try { // Преобразование JSON-строки в документ MongoDB
            return Document.parse(json)}
        catch (e: Exception) {
            IhLogger.log("Ошибка конвертации ${obj.javaClass.simpleName} в документ: $e")
        }

        return null
    }
}
