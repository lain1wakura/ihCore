package org.imperial_hell.qbrp.Blocks

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Box
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import org.imperial_hell.qbrp.Utils.qbTimer

class ChunkUnloader(
    private val world: World, // Мир, к которому относится чанк
    private val chunk: Chunk,       // Чанк для проверки
    private val chunkHandler: ChuckHandler, // Обработчик чанков
) {
    private val playerRange: Int = 64 // Диапазон проверки игроков (по умолчанию 64 блока)
    private val unloadCycleDuration = 15
    private var unloadCycle = qbTimer(20 * unloadCycleDuration) { checkout() }.start()

    fun checkout() {
        // Проверяем, загружен ли чанк
        if (!world.isChunkLoaded(chunk.pos.x, chunk.pos.z)) {
            // Если чанк уже выгружен, останавливаем цикл и удаляем объект
            unloadCycle.destroy()
            return
        }

        // Получаем границы чанка через ChunkHandler
        val chunkBounds = chunkHandler.getChunkBounds(chunk)
        val chunkBox = Box(chunkBounds.first, chunkBounds.second)

        // Проверяем, находятся ли игроки в пределах диапазона
        val playersNearby = world.players.any { player ->
            val playerPos = player.blockPos
            val playerBox = Box(playerPos.add(-playerRange, -playerRange, -playerRange), playerPos.add(playerRange, playerRange, playerRange))
            playerBox.intersects(chunkBox)
        }

        if (!playersNearby) {
            // Если игроков рядом нет, выгружаем чанк через обработчик
            chunkHandler.unloadChunk(chunk)
        }
    }
}
