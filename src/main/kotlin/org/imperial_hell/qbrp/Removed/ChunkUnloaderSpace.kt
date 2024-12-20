//package org.imperial_hell.qbrp.Blocks
//
//import net.minecraft.world.World
//import net.minecraft.util.math.ChunkPos
//import net.minecraft.world.chunk.Chunk
//import java.util.concurrent.ConcurrentHashMap
//
//object ChunkUnloaderSpace {
//    private val unloaders = ConcurrentHashMap<ChunkPos, ChunkUnloader>() // Хранение анлоадеров по позиции чанка
//
//    /**
//     * Проверяет, существует ли анлоадер для указанного чанка.
//     */
//    fun hasUnloader(chunkPos: ChunkPos): Boolean {
//        return unloaders.containsKey(chunkPos)
//    }
//
//    fun getUnloader(chunkPos: ChunkPos): ChunkUnloader {
//        return unloaders[chunkPos]!!
//    }
//
//    /**
//     * Добавляет анлоадер для указанного чанка, если его ещё нет.
//     */
//    fun addUnloader(unloader: ChunkUnloader, chunkPos: ChunkPos) {
//        if (!hasUnloader(chunkPos)) {
//            unloaders[chunkPos] = unloader
//        }
//    }
//}
