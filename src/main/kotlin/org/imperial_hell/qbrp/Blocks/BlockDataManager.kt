package org.imperial_hell.qbrp.Blocks

import net.minecraft.util.math.BlockPos
import org.imperial_hell.ihSystems.IhLogger
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class BlockDataManager(
    val dbService: qbBlocksService,
) {

    val blocks = ConcurrentHashMap<BlockPos, qbBlock>()

    fun add(pos: BlockPos, data: qbBlock) {
        blocks[pos] = data
        dbService.exportBlock(data, pos)
        IhLogger.log("Блок добавлен: <<${pos}>> с данными: <<${data}>>", IhLogger.MessageType.INFO, debugMode = true)
    }

    fun get(pos: BlockPos): qbBlock? {
        IhLogger.log("Запрашивается блок на позиции: <<${pos}>>", IhLogger.MessageType.INFO, debugMode = true)
        dbService.importBlock(pos)
        val block = blocks[pos]
        return block
    }

    fun update(pos: BlockPos, data: qbBlock) {
        blocks[pos] = data
        dbService.exportBlock(data, pos)
    }

    fun updateJson(pos: BlockPos, data: qbBlock) {
        blocks[pos] = data
        dbService.exportBlock(data, pos)
    }

    fun remove(pos: BlockPos) {
        dbService.exportBlock(get(pos) as qbBlock, pos)
        blocks.remove(pos)
        IhLogger.log("Блок удален на позиции: <<${pos}>>", IhLogger.MessageType.INFO, debugMode = true)
    }

    fun destroy(pos: BlockPos) {
        remove(pos)
        dbService.updateKey(pos, UUID.randomUUID().toString())
    }

    fun removeAll() {
        blocks.forEach { pos, block ->
            remove(pos)
        }
        blocks.clear()
    }

    fun registerBlock(blockPos: BlockPos, blockData: qbBlock) {
        dbService.exportBlock(blockData, blockPos)
        blocks[blockPos] = blockData
        IhLogger.log("Зарегистрирован блок на позиции: <<${blockPos}>> с данными: <<${blockData}>>", IhLogger.MessageType.INFO, debugMode = true)
    }

    fun removeBlocksInRange(startPos: BlockPos, endPos: BlockPos) {
        IhLogger.log("Удаление блоков в диапазоне: <<$startPos>> - <<$endPos>>", debugMode = true)
        blocks.keys.removeIf { pos ->
            val isInRange = pos.x in startPos.x..endPos.x &&
                    pos.y in startPos.y..endPos.y &&
                    pos.z in startPos.z..endPos.z
            if (isInRange) {
                dbService.exportBlock(blocks[pos] as qbBlock, pos)
                true // Удаление блоков производится в maps на следующем проходе итератора
            } else {
                false
            }
        }
    }

}
