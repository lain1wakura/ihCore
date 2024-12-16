package org.imperial_hell.common.Packets

import net.minecraft.network.PacketByteBuf
import org.imperial_hell.common.Proxy.ProximityPlayerData

// Класс пакета для передачи данных игрока (PlayerData)
class PlayerDataPacket(
    val proximityPlayerData: ProximityPlayerData = ProximityPlayerData.getBlankPlayerData("")
) : IhPacket() {

    // Сериализация данных (запись в PacketByteBuf)
    override fun write() {
        buf.writeString(proximityPlayerData.playerUuid)

        // Сериализуем состояние (state)
        buf.writeString(proximityPlayerData.state.name)  // Сохраняем состояние (AFK или TYPING)

        // Сериализуем прогресс набора текста
        buf.writeString(proximityPlayerData.messageTypingProgress)

        // Сериализуем уникальный идентификатор персонажа
        buf.writeString(proximityPlayerData.characterUuid)

        // Сериализуем описание внешности
        buf.writeString(proximityPlayerData.appearanceDesc)
    }

    // Десериализация данных (чтение из PacketByteBuf)
    override fun readHandle(buffer: PacketByteBuf): ProximityPlayerData {
        val playerUuid = buffer.readString(32767)

        val state = ProximityPlayerData.State.valueOf(buffer.readString(32767))  // Читаем состояние (AFK или TYPING)

        // Чтение прогресса набора текста
        val messageTypingProgress = buffer.readString(32767)

        // Чтение уникального идентификатора персонажа
        val characterUuid = buffer.readString(32767)

        // Чтение описания внешности
        val appearanceDesc = buffer.readString(32767)

        // Возвращаем новый объект PlayerDataPacket с прочитанными данными
        return ProximityPlayerData(
            playerUuid,
            state,
            messageTypingProgress,
            characterUuid,
            appearanceDesc
        )
    }

    // Метод для обработки пакета
    override fun apply() {
        // В этой части вы можете применить полученные данные
        // Например, обновить состояние игрока на клиенте или сервере
        println("Получены данные игрока: $proximityPlayerData")
    }
}
