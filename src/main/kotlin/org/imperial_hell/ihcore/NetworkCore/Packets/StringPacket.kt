package org.imperial_hell.ihcore.NetworkCore.Packets

// Абстрактный класс для пакетов с сообщениями
class StringPacket(
   val message: String = "",
    ) : IhPacket() {

    override fun write() {
        super.write()
        buf.writeString(message)
    }

    override fun read(): String { return buf.readString() }

    override fun apply() {}
}
