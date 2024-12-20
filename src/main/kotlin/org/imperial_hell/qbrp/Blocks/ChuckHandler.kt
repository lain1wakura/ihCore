package org.imperial_hell.qbrp.Blocks

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import org.imperial_hell.common.Blocks.qbBlock
import org.imperial_hell.ihSystems.IhLogger
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class ChuckHandler(val blocksManager: BlockDataManager) {

    fun register() {
        ServerChunkEvents.CHUNK_LOAD.register { world: World, chunk: Chunk ->
//            IhLogger.log("<<-    ЗАГРУЗКА ЧАНКА    ->>")
//            IhLogger.log("<<-    ${chunk.pos}    ->>")
            loadChunk(world, chunk)
        }
        ServerChunkEvents.CHUNK_UNLOAD.register { world: World, chunk: Chunk ->
            unloadChunk(chunk)
        }
        PlayerBlockBreakEvents.AFTER.register(PlayerBlockBreakEvents.After { world, player, pos, state, blockEntity ->
            onBlockBreak(world, player, pos, state)
        })
    }

    fun onBlockBreak(world: World, player: PlayerEntity, pos: BlockPos, state: BlockState) {
        if (blocksManager.get(pos) != null) {
            blocksManager.destroy(pos)
        }
    }

    fun loadChunk(world: World, chunk: Chunk) {
        CompletableFuture.runAsync {
            getDataBlocks(world, chunk).forEach { data ->
                blocksManager.add(data.first, data.second)
            }
        }.exceptionally { ex ->
            IhLogger.log("Ошибка при загрузке чанка: ${ex.message}", IhLogger.MessageType.ERROR)
            null
        }
    }

    fun unloadChunk(chunk: Chunk) {
        CompletableFuture.runAsync {
//            IhLogger.log("<<-    ВЫГРУЗКА ЧАНКА    ->>")
//            IhLogger.log("<<-    ${chunk.pos}    ->>")
            val (start, end) = getChunkBounds(chunk)
            blocksManager.removeBlocksInRange(start, end)
        }.exceptionally { ex ->
            IhLogger.log("Ошибка при выгрузке чанка: ${ex.message}", IhLogger.MessageType.ERROR)
            null
        }
    }

    fun getDataBlocks(world: World, pos: BlockPos): List<Pair<BlockPos, qbBlock>> {
       return getDataBlocks(world, world.getChunk(pos))
    }

    fun getDataBlocks(world: World, chunk: Chunk): List<Pair<BlockPos, qbBlock>> {
        val (start, end) = getChunkBounds(chunk)

        // Выполняем получение валидных позиций в асинхронном режиме
        val validPositionsFuture = CompletableFuture.supplyAsync {
            blocksManager.dbService.getValidPositions(
                start.x, start.z, end.x, end.z
            )
        }

        return try {
            val validPositions = validPositionsFuture.get() // Ожидаем завершения выполнения

            validPositions.mapNotNull { pos ->
                IhLogger.log("Проверяем позицию <<$pos>>", debugMode = true)
                val blockState = world.getBlockState(pos)

                // Проверяем, не является ли блок воздухом
                if (!blockState.isAir) {
                    val block = blocksManager.dbService.importBlock(pos)
                    block?.let { pos to it }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            IhLogger.log("Ошибка при получении данных блоков: ${e.message}", IhLogger.MessageType.ERROR)
            emptyList()
        }
    }

    fun getChunkBounds(chunk: Chunk): Pair<BlockPos, BlockPos> {
        val chunkPos = chunk.pos // Получаем позицию чанка

        // Вычисляем координаты начала и конца чанка
        val startX = chunkPos.startX // Минимальная X-координата
        val startZ = chunkPos.startZ // Минимальная Z-координата
        val endX = chunkPos.endX    // Максимальная X-координата
        val endZ = chunkPos.endZ    // Максимальная Z-координата

        // Возвращаем диапазон координат (нижний левый угол, верхний правый угол)
        return Pair(
            BlockPos(startX, 0, startZ),  // Нижний левый угол (минимальная высота)
            BlockPos(endX, 255, endZ)    // Верхний правый угол (максимальная высота)
        )
    }
}
