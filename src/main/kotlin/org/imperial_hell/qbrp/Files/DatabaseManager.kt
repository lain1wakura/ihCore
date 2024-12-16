package org.imperial_hell.qbrp.Files

import org.litote.kmongo.KMongo
import com.mongodb.client.MongoDatabase
import com.mongodb.client.MongoClient
import com.mongodb.MongoException
import org.bson.Document
import org.bson.conversions.Bson
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.imperial_hell.ihSystems.IhLogger

class DatabaseManager(private val url: String, private val databaseName: String) {

    private var client: MongoClient? = null
    private var database: MongoDatabase? = null
    var error = ""

    /**
     * Подключение к базе данных.
     */
    fun connect(): Boolean {
        try {
            client = KMongo.createClient(url)
            database = client?.getDatabase(databaseName)
            return true
        } catch (e: MongoException) {
            error = e.message ?: ""
            return false
        }
    }

    /**
     * Закрытие соединения с базой данных.
     */
    fun disconnect() {
        client?.close()
        println("Соединение с базой данных закрыто.")
    }

    // Геттер для базы данных
    val db: MongoDatabase?
        get() = database

    fun updateFieldInDocument(document: String, query: Map<String, Any>, field: String, newValue: Any): Boolean {
        return try {
            val collection = database?.getCollection(document)
            // Создаем фильтр для поиска документа
            val filter: Bson = Filters.and(query.map { Filters.eq(it.key, it.value) })

            // Создаем обновление, где обновляется только указанное поле
            val updateDocument = Document("\$set", Document(field, newValue))

            // Выполняем операцию обновления
            val result = collection?.updateOne(filter, updateDocument)

            // Печатаем результат
            IhLogger.log("Обновлено ${result?.modifiedCount} документов", IhLogger.MessageType.INFO, debugMode = true)
            (result?.modifiedCount ?: 0) > 0
        } catch (e: MongoException) {
            IhLogger.log("Ошибка обновления поля: ${e.message}", IhLogger.MessageType.ERROR)
            false
        }
    }

    fun updateChangedFields(document: String, query: Map<String, Any>, updates: Map<String, Any>): Boolean {
        return try {
            val collection = database?.getCollection(document)
            val filter: Bson = Filters.and(query.map { Filters.eq(it.key, it.value) })

            // Найти текущий документ в коллекции
            val currentDocument = collection?.find(filter)?.firstOrNull()

            if (currentDocument != null) {
                // Подготовить изменения, только если они отличаются от текущих данных
                val changes = updates.filter { (key, newValue) ->
                    currentDocument[key] != newValue
                }

                if (changes.isNotEmpty()) {
                    // Создать документ для обновления только измененных полей
                    val updateDocument = Document("\$set", Document(changes))
                    val result = collection.updateOne(filter, updateDocument)

                    IhLogger.log(
                        "Обновлено ${result?.modifiedCount} документов с измененными полями: $changes",
                        IhLogger.MessageType.INFO,
                        debugMode = true
                    )
                    return (result?.modifiedCount ?: 0) > 0
                } else {
                    IhLogger.log("Нет изменений для обновления.", IhLogger.MessageType.INFO, debugMode = true)
                    return true
                }
            } else {
                IhLogger.log("Документ не найден для обновления.", IhLogger.MessageType.WARN, debugMode = true)
                return false
            }
        } catch (e: MongoException) {
            IhLogger.log("Ошибка обновления измененных полей: ${e.message}", IhLogger.MessageType.ERROR)
            false
        }
    }


    fun upsertData(document: String, query: Map<String, Any>, updates: Map<String, Any>): Boolean {
        return try {
            val collection = database?.getCollection(document)
            val filter: Bson = Filters.and(query.map { Filters.eq(it.key, it.value) })

            // Проверяем, существует ли документ
            val existingDocument = collection?.find(filter)?.firstOrNull()

            if (existingDocument != null) {
                // Если документ существует, обновляем только измененные поля
                return updateChangedFields(document, query, updates)
            } else {
                // Если документ не существует, вставляем новый
                val updateDocument = Document("\$set", Document(updates))
                val options = UpdateOptions().upsert(true)

                val result = collection?.updateOne(filter, updateDocument, options)
                IhLogger.log(
                    "Upsert выполнен. Вставлено: <<${result?.upsertedId}>>",
                    IhLogger.MessageType.INFO,
                    debugMode = true
                )
                return result != null
            }
        } catch (e: MongoException) {
            IhLogger.log("Ошибка upsert операции: ${e.message}", IhLogger.MessageType.ERROR)
            false
        }
    }

    /**
     * Вставка данных в коллекцию.
     */
    fun insertData(document: String, data: Map<String, Any>, comment: String = ""): String? {
        return try {
            val collection = database?.getCollection(document)
            val result = collection?.insertOne(Document(data))
            IhLogger.log("Данные успешно добавлены. ID: <<${result?.insertedId}>>", IhLogger.MessageType.INFO, debugMode = true)
            result?.insertedId?.asObjectId()?.value?.toHexString()
        } catch (e: MongoException) {
            IhLogger.log("Ошибка вставки данных: ${e.message}", IhLogger.MessageType.ERROR)
            null
        }
    }

    /**
     * Обновление данных в коллекции.
     */
    fun updateData(document: String, query: Map<String, Any>, update: Map<String, Any>, comment: String = ""): Boolean {
        return try {
            val collection = database?.getCollection(document)
            val filter: Bson = Filters.and(query.map { Filters.eq(it.key, it.value) })
            val result = collection?.updateOne(
                filter,
                Document("\$set", Document(update))
            )
            IhLogger.log("Обновлено ${result?.modifiedCount} документов.", IhLogger.MessageType.INFO, debugMode = true)
            (result?.modifiedCount ?: 0) > 0
        } catch (e: MongoException) {
            IhLogger.log("Ошибка обновления данных: ${e.message}", IhLogger.MessageType.ERROR)
            false
        }
    }

    /**
     * Удаление данных из коллекции.
     */
    fun deleteData(document: String, query: Map<String, Any>, comment: String = ""): Boolean {
        return try {
            val collection = database?.getCollection(document)
            val filter: Bson = Filters.and(query.map { Filters.eq(it.key, it.value) })
            val result = collection?.deleteOne(filter)
            println("Удалено ${result?.deletedCount} документов.")
            IhLogger.log("Удалено ${result?.deletedCount} документов.", IhLogger.MessageType.INFO, debugMode = true)
            (result?.deletedCount ?: 0) > 0
        } catch (e: MongoException) {
            IhLogger.log("Ошибка удаления данных: ${e.message}", IhLogger.MessageType.WARN)
            false
        }
    }

    /**
     * Получение всех данных из коллекции.
     */
    fun fetchAll(document: String, query: Map<String, Any>): List<Document> {
        return try {
            val collection = database?.getCollection(document)
            val filter: Bson = Filters.and(query.map { Filters.eq(it.key, it.value) })
            collection?.find(filter)?.toList() ?: emptyList()
        } catch (e: MongoException) {
            IhLogger.log("Ошибка получения данных: ${e.message}", IhLogger.MessageType.ERROR)
            emptyList()
        }
    }

    /**
     * Получение одного документа из коллекции.
     */
    fun fetchOne(document: String, query: Map<String, Any>): Document? {
        return try {
            val collection = database?.getCollection(document)
            val filter: Bson = Filters.and(query.map { Filters.eq(it.key, it.value) })
            collection?.find(filter)?.firstOrNull()
        } catch (e: MongoException) {
            IhLogger.log("Ошибка получения данных: ${e.message}", IhLogger.MessageType.ERROR)
            null
        }
    }
}
