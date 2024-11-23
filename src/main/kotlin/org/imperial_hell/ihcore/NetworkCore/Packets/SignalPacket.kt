package org.imperial_hell.ihcore.NetworkCore.Packets

// Абстрактный класс для пакетов с сообщениями
class SignalPacket() : IhPacket() {

    override fun apply() {
        println("Получен сигнал")
    }
}
