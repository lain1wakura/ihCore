package org.imperial_hell.ihcore.Networking.Packets

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.ihcore.Sync.ProximityPlayerData

// Абстрактный класс для пакетов с сообщениями
abstract class IhPacket() {

    // Счётчик для ограничения записи
    protected var writeCount = 0
    protected var WRITE_LIMIT = 1
    protected var readResult: Any? = null
    var buf: PacketByteBuf = PacketByteBufs.create()

    // Метод для сериализации данных (запись сообщения в PacketByteBuf)
    open fun write() {
        if (writeCount >= WRITE_LIMIT) {
            throw IllegalStateException("Запись данных в буфер уже была произведена")
        }
        writeCount++
    }

    fun read(): Any? {
        try {
            // Создаем PacketByteBuf из копии исходного буфера
            val copiedBuf = PacketByteBuf(buf.copy())
            readResult = readHandle(copiedBuf)
        } catch (e: Exception) {
            IhLogger.log("Ошибка при чтении пакета: $e", type = IhLogger.MessageType.ERROR)
        }
        return readResult
    }


    open fun readHandle(buffer: PacketByteBuf): Any {
        return buffer
    }

    fun setBuffer(buf: PacketByteBuf) {
        this.buf = buf
    }

    // Абстрактный метод apply, который должен быть реализован в подклассе для обработки полученного пакета
    abstract fun apply()
}
