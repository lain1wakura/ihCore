//package org.imperial_hell.qbrp.Networking.Packets
//
//import net.minecraft.network.PacketByteBuf
//import org.imperial_hell.qbrp.Removed.Skins.SkinManager.Model
//
//// Класс пакета для передачи десериализованной текстуры скина
//class SkinPacket(
//    val playerName: String = "",
//    val skinTextureData: ByteArray = byteArrayOf(),  // Бинарные данные текстуры скина
//    val model: Model = Model.CLASSIC
//) : IhPacket() {
//
//    // Сериализация данных (запись в PacketByteBuf)
//    override fun write() {
//        buf.writeString(playerName)  // Записываем имя игрока
//        buf.writeByteArray(skinTextureData)  // Записываем бинарные данные скина
//        buf.writeString(model.value)  // Записываем модель (CLASSIC или SLIM)
//    }
//
//    // Десериализация данных (чтение из PacketByteBuf)
//    override fun readHandle(buffer: PacketByteBuf): Any {
//        val name = buffer.readString(32767)
//        val skinData = buffer.readByteArray()  // Читаем бинарные данные текстуры
//        val model = Model.fromString(buffer.readString(32767)) ?: Model.CLASSIC
//        return SkinPacket(name, skinData, model)  // Возвращаем пакет с прочитанными данными
//    }
//
//    // Метод для обработки пакета
//    override fun apply() {
//        // Дальш вы можете использовать nativeImage для применения скина к игроку
//        // Это будет зависеть от вашей реализации скинов на сервере или клиенте.
//        println("У игрока $playerName будет установлен скин с моделью: $model")
//    }
//}
