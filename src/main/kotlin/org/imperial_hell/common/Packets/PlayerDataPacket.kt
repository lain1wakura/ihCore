package org.imperial_hell.common.Packets

import net.minecraft.network.PacketByteBuf
import org.imperial_hell.common.Proxy.ProxyPlayerData

// Класс пакета для передачи данных игрока (PlayerData)
class PlayerDataPacket(
    val proxyPlayerData: ProxyPlayerData = ProxyPlayerData.getBlankPlayerData("")
) : IhPacket() {

    // Сериализация данных (запись в PacketByteBuf)
    override fun write() {
        buf.writeString(proxyPlayerData.playerUuid)

        // Сериализуем состояние (state)
        buf.writeString(proxyPlayerData.state.name)  // Сохраняем состояние (AFK или TYPING)

        // Сериализуем прогресс набора текста
        buf.writeString(proxyPlayerData.messageTypingProgress)

        // Сериализуем уникальный идентификатор персонажа
        buf.writeString(proxyPlayerData.characterUuid)

        // Сериализуем описание внешности
        buf.writeString(proxyPlayerData.appearanceDesc)
    }

    // Десериализация данных (чтение из PacketByteBuf)
    override fun readHandle(buffer: PacketByteBuf): ProxyPlayerData {
        val playerUuid = buffer.readString(32767)

        val state = ProxyPlayerData.State.valueOf(buffer.readString(32767))  // Читаем состояние (AFK или TYPING)

        // Чтение прогресса набора текста
        val messageTypingProgress = buffer.readString(32767)

        // Чтение уникального идентификатора персонажа
        val characterUuid = buffer.readString(32767)

        // Чтение описания внешности
        val appearanceDesc = buffer.readString(32767)

        // Возвращаем новый объект PlayerDataPacket с прочитанными данными
        return ProxyPlayerData(
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
        println("Получены данные игрока: $proxyPlayerData")
    }
}
