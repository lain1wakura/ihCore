package org.imperial_hell.ihcore.NetworkCore.Packets

import org.imperial_hell.ihcore.Sync.ProximityPlayerData

// Класс пакета для передачи данных игрока (PlayerData)
class PlayerDataPacket(
    val proximityPlayerData: ProximityPlayerData
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
    override fun read(): Any? {
        val playerUuid = buf.readString(32767)

        val state = ProximityPlayerData.State.valueOf(buf.readString(32767))  // Читаем состояние (AFK или TYPING)

        // Чтение прогресса набора текста
        val messageTypingProgress = buf.readString(32767)

        // Чтение уникального идентификатора персонажа
        val characterUuid = buf.readString(32767)

        // Чтение описания внешности
        val appearanceDesc = buf.readString(32767)

        // Возвращаем новый объект PlayerDataPacket с прочитанными данными
        return PlayerDataPacket(
            ProximityPlayerData(
                playerUuid,
                state,
                messageTypingProgress,
                characterUuid,
                appearanceDesc
            )
        )
    }

    // Метод для обработки пакета
    override fun apply() {
        // В этой части вы можете применить полученные данные
        // Например, обновить состояние игрока на клиенте или сервере
        println("Получены данные игрока: $proximityPlayerData")
    }
}
