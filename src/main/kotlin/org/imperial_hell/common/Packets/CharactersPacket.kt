package org.imperial_hell.qbrp.Networking

import net.minecraft.network.PacketByteBuf
import org.imperial_hell.common.Packets.IhPacket

class CharactersPacket(
    val dataList: List<Map<String, Any>> = emptyList(),
) : IhPacket() {


    // Сериализация данных
     override fun write() {
        buf.writeInt(dataList.size)  // Записываем количество элементов в списке

        for (dataMap in dataList) {
            buf.writeInt(dataMap.size)  // Записываем количество пар ключ-значение в карте

            for ((key, value) in dataMap) {
                buf.writeString(key)  // Записываем ключ

                when (value) {
                    is String -> {
                        buf.writeByte(0)  // Тип 0 для строки
                        buf.writeString(value)
                    }
                    is Int -> {
                        buf.writeByte(1)  // Тип 1 для целого числа
                        buf.writeInt(value)
                    }
                    is Double -> {
                        buf.writeByte(2)  // Тип 2 для числа с плавающей точкой
                        buf.writeDouble(value)
                    }
                    is Boolean -> {
                        buf.writeByte(3)  // Тип 3 для булевого значения
                        buf.writeBoolean(value)
                    }
                    is Long -> {
                        buf.writeByte(4)  // Тип 4 для длинного целого числа
                        buf.writeLong(value)
                    }
                    else -> throw IllegalArgumentException("Не поддерживаемый тип значения: ${value::class}")
                }
            }
        }
    }

    // Десериализация данных
    override fun apply() {
        // Пример обработки пакета на сервере или клиенте
        println("Получены данные: $dataList")
    }

    // Статический метод для создания пакета из буфера
     override fun readHandle(buffer: PacketByteBuf): List<Map<String, Any>> {

        val size = buffer.readInt()  // Считываем количество элементов
        val dataList = mutableListOf<Map<String, Any>>()

        for (i in 0 until size) {
            if (buffer.readableBytes() < 4) {
                throw IllegalStateException("Недостаточно данных для размера карты")
            }
            val mapSize = buffer.readInt()  // Считываем размер карты
            val dataMap = mutableMapOf<String, Any>()

            for (j in 0 until mapSize) {
                if (buffer.readableBytes() < 2) {
                    throw IllegalStateException("Недостаточно данных для пары ключ-значение")
                }
                val key = buffer.readString()  // Читаем ключ
                val type = buffer.readByte()   // Читаем тип значения
                val value = when (type) {
                    0.toByte() -> buffer.readString()  // Строка
                    1.toByte() -> buffer.readInt()     // Целое число
                    2.toByte() -> buffer.readDouble()  // Дробное число
                    3.toByte() -> buffer.readBoolean() // Логическое значение
                    4.toByte() -> buffer.readLong()    // Длинное число
                    else -> throw IllegalArgumentException("Неизвестный тип значения: $type")
                }
                dataMap[key] = value
            }
            dataList.add(dataMap)
        }
        return dataList
    }

}
