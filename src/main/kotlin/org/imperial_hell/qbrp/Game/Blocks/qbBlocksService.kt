package org.imperial_hell.qbrp.Game.Blocks

import net.minecraft.util.math.BlockPos
import org.bson.Document
import org.imperial_hell.common.Blocks.qbBlock
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.System.Files.DatabaseManager
import org.imperial_hell.qbrp.System.Files.MongoConverter

class qbBlocksService(val dbManager: DatabaseManager) {

    fun getKey(uuid: String): Document? {
        return this@qbBlocksService.dbManager.fetchOne("blocks", mapOf("key" to uuid))
    }

    fun getKey(pos: BlockPos): Document? {
        val key = this@qbBlocksService.dbManager.fetchOne("blocks", mapOf("x" to pos.x, "y" to pos.y, "z" to pos.z))
        return key
    }

    fun updateKey(pos: BlockPos, newKey: String) {
        this@qbBlocksService.dbManager.upsertData("blocks", mapOf("x" to pos.x, "y" to pos.y, "z" to pos.z), mapOf("key" to newKey))
    }

    fun getValidPositions(x1: Int, z1: Int, x2: Int, z2: Int): List<BlockPos> {
        val (minX, maxX) = if (x1 <= x2) x1 to x2 else x2 to x1
        val (minZ, maxZ) = if (z1 <= z2) z1 to z2 else z2 to z1
        val query = mapOf(
            "x" to mapOf("\$gte" to minX, "\$lte" to maxX),
            "z" to mapOf("\$gte" to minZ, "\$lte" to maxZ)
        )
        // Выполняем запрос в коллекции "blocks"
        val documents = this@qbBlocksService.dbManager.fetchAll("blocks", query)
        return documents.map { doc ->
            val x = doc.getInteger("x")
            val y = doc.getInteger("y")
            val z = doc.getInteger("z")
            BlockPos(x, y, z)
        }
    }

    fun importBlock(pos: BlockPos): qbBlock? {
        IhLogger.log("Импортирование блока на позиции<<$pos>>", debugMode = true)
        val key = getKey(pos)
        return importBlock(key?.get("key", String::class.java) as String)
    }

    fun importBlock(uuid: String): qbBlock? {
        val block = this@qbBlocksService.dbManager.fetchOne("data", mapOf("uuid" to uuid))
        if (block != null) {
            return MongoConverter.convert<qbBlock>(block, qbBlock::class)
        } else {
            IhLogger.log("Ошибка загрузки блока с UUID: <<$uuid>>", IhLogger.MessageType.ERROR)
            return null
        }
    }

    fun exportBlock(block: qbBlock, pos: BlockPos) {
        val document = MongoConverter.toDocument(block)
        this@qbBlocksService.dbManager.upsertData("data", mapOf("uuid" to block.uuid), document?.toMap() ?: mapOf())
        this@qbBlocksService.dbManager.upsertData("blocks", mapOf("x" to pos.x, "y" to pos.y, "z" to pos.z), mapOf("key" to block.uuid, "x" to pos.x, "y" to pos.y, "z" to pos.z))
        IhLogger.log("Конвертирован блок: <<${block.uuid}>>", debugMode = true)
    }

}