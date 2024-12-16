package org.imperial_hell.qbrp.Networking.Packets

import net.minecraft.network.PacketByteBuf

// Абстрактный класс для пакетов с сообщениями
class SignalPacket() : IhPacket() {

    override fun apply() {
        println("Получен сигнал")
    }

    override fun readHandle(buffer: PacketByteBuf): Signal {
        return Signal()
    }
}
