package org.imperial_hell.ihcore.Networking.Packets

import net.minecraft.network.PacketByteBuf

// Абстрактный класс для пакетов с сообщениями
class StringPacket(
   val message: String = "",
    ) : IhPacket() {

    override fun write() {
        super.write()
        buf.writeString(message)
    }

    override fun readHandle(buffer: PacketByteBuf): String { return buffer.readString() }

    override fun apply() {}
}
