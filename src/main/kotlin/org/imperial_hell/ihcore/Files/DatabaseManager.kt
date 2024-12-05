package org.imperial_hell.ihcore.Files

import com.mongodb.MongoClientException
import org.litote.kmongo.KMongo
import com.mongodb.client.MongoDatabase
import com.mongodb.client.MongoClient
import com.mongodb.MongoException
import org.bson.Document
import org.bson.conversions.Bson
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
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
        }
        return false
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
            println("Обновлено ${result?.modifiedCount} документов.")
            (result?.modifiedCount ?: 0) > 0
        } catch (e: MongoException) {
            println("Ошибка обновления поля: ${e.message}")
            false
        }
    }

    fun upsertData(document: String, query: Map<String, Any>, update: Map<String, Any>): Boolean {
        return try {
            val collection = database?.getCollection(document)
            val filter: Bson = Filters.and(query.map { Filters.eq(it.key, it.value) })
            val updateDocument = Document("\$set", Document(update))
            val options = UpdateOptions().upsert(true)

            val result = collection?.updateOne(filter, updateDocument, options)
            println("Upsert выполнен. Изменено: ${result?.modifiedCount}, Вставлено: ${result?.upsertedId}")
            result != null
        } catch (e: MongoException) {
            println("Ошибка upsert операции: ${e.message}")
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
            println("Данные успешно добавлены. ID: ${result?.insertedId}")
            result?.insertedId?.asObjectId()?.value?.toHexString()
        } catch (e: MongoException) {
            println("Ошибка вставки данных: ${e.message}")
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
            println("Обновлено ${result?.modifiedCount} документов.")
            (result?.modifiedCount ?: 0) > 0
        } catch (e: MongoException) {
            println("Ошибка обновления данных: ${e.message}")
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
            (result?.deletedCount ?: 0) > 0
        } catch (e: MongoException) {
            println("Ошибка удаления данных: ${e.message}")
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
            println("Ошибка получения данных: ${e.message}")
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
            println("Ошибка получения данных: ${e.message}")
            null
        }
    }
}
