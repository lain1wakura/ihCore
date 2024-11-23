package org.imperial_hell.ihcore.NetworkCore.Packets

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf

// Абстрактный класс для пакетов с сообщениями
abstract class IhPacket() {

    // Счётчик для ограничения записи
    protected var writeCount = 0
    protected var WRITE_LIMIT = 1
    var buf: PacketByteBuf = PacketByteBufs.create()

    // Метод для сериализации данных (запись сообщения в PacketByteBuf)
    open fun write() {
        if (writeCount >= WRITE_LIMIT) {
            throw IllegalStateException("Запись данных в буфер уже была произведена")
        }
        writeCount++
    }

    open fun read(): Any? {
        return null
    }

    fun setBuffer(buf: PacketByteBuf) {
        this.buf = buf
    }

    // Абстрактный метод apply, который должен быть реализован в подклассе для обработки полученного пакета
    abstract fun apply()
}
