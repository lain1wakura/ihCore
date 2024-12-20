package org.imperial_hell.qbrp.Blocks

import net.minecraft.util.math.BlockPos
import org.bson.Document
import org.imperial_hell.common.Blocks.qbBlock
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Files.DatabaseManager
import org.imperial_hell.qbrp.Files.MongoConverter

class qbBlocksService(val databaseManager: DatabaseManager) {

    fun getKey(uuid: String): Document? {
        return databaseManager.fetchOne("blocks", mapOf("key" to uuid))
    }

    fun getKey(pos: BlockPos): Document? {
        val key = databaseManager.fetchOne("blocks", mapOf("x" to pos.x, "y" to pos.y, "z" to pos.z))
        return key
    }

    fun updateKey(pos: BlockPos, newKey: String) {
        databaseManager.upsertData("blocks", mapOf("x" to pos.x, "y" to pos.y, "z" to pos.z), mapOf("key" to newKey))
    }

    fun getValidPositions(x1: Int, z1: Int, x2: Int, z2: Int): List<BlockPos> {
        //IhLogger.log("Ищем позиции блоков по координатам: <<$x1, $z1 : $x2, $z2>>", debugMode = true)

        // Убедимся, что диапазон указан правильно
        val (minX, maxX) = if (x1 <= x2) x1 to x2 else x2 to x1
        val (minZ, maxZ) = if (z1 <= z2) z1 to z2 else z2 to z1
        //IhLogger.log("Диапазон координат вычислен: X от $minX до $maxX, Z от $minZ до $maxZ", debugMode = true)

//        val a = -1000
//        val b = 1000

        // Формируем запрос к MongoDB для поиска по диапазону координат
        val query = mapOf(
            "x" to mapOf("\$gte" to minX, "\$lte" to maxX),
            "z" to mapOf("\$gte" to minZ, "\$lte" to maxZ)
        )
        //IhLogger.log("Сформирован запрос к базе данных: $query", debugMode = true)

        // Выполняем запрос в коллекции "blocks"
        val documents = databaseManager.fetchAll("blocks", query)
        //IhLogger.log("Найдено документов: ${documents.size}", debugMode = true)

        // Преобразуем найденные документы в список BlockPos
        return documents.map { doc ->
            val x = doc.getInteger("x")
            val y = doc.getInteger("y")
            val z = doc.getInteger("z")
            //IhLogger.log("Обрабатываем документ: x=$x, y=$y, z=$z", debugMode = true)
            BlockPos(x, y, z)
        }

    }


    fun importBlock(pos: BlockPos): qbBlock? {
        IhLogger.log("Импортирование блока на позиции<<$pos>>", debugMode = true)
        val key = getKey(pos)
        return importBlock(key?.get("key", String::class.java) as String)
    }

    fun importBlock(uuid: String): qbBlock? {
        val block = databaseManager.fetchOne("data", mapOf("uuid" to uuid))
        if (block != null) {
            return MongoConverter.convert<qbBlock>(block, qbBlock::class)
        } else {
            IhLogger.log("Ошибка загрузки блока с UUID: <<$uuid>>", IhLogger.MessageType.ERROR)
            return null
        }
    }

    fun exportBlock(block: qbBlock, pos: BlockPos) {
        val document = MongoConverter.toDocument(block)
        databaseManager.upsertData("data", mapOf("uuid" to block.uuid), document?.toMap() ?: mapOf())
        databaseManager.upsertData("blocks", mapOf("x" to pos.x, "y" to pos.y, "z" to pos.z), mapOf("key" to block.uuid, "x" to pos.x, "y" to pos.y, "z" to pos.z))
        IhLogger.log("Конвертирован блок: <<${block.uuid}>>", debugMode = true)
    }

}