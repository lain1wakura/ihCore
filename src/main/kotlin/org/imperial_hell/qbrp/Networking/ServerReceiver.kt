package org.imperial_hell.qbrp.Networking

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Networking.Packets.IhPacket

class ServerReceiver<T>(
    val messageId: Identifier,
    val callback: (T, ServerReceiverContext) -> Unit
) {

    // Метод для регистрации глобального приемника пакетов
    inline fun <reified P : IhPacket> register() {
        ServerPlayNetworking.registerGlobalReceiver(messageId) { server, player, handler, buf, responseSender ->
            // Создаем пакет через рефлексию
            val packet = createPacket<P>(buf)

            // Извлекаем данные типа T
            val data = packet.read() as T

            // Создаем объект ReceiverContext
            val context = ServerReceiverContext(server, player, handler, responseSender)
            handle(data, context)
            callback(data, context) // Передаем данные и контекст в callback
        }
    }

    // Метод для создания пакета через рефлексию
    inline fun <reified P : IhPacket> createPacket(buf: net.minecraft.network.PacketByteBuf): P {
        // Находим подходящий конструктор
        val constructor = P::class.constructors.firstOrNull()
            ?: throw IllegalArgumentException("No constructor found for packet class ${P::class}")

        // Создаем экземпляр с использованием callBy (чтобы параметры с значениями по умолчанию работали)
        val packet = constructor.callBy(emptyMap()) as? P
            ?: throw IllegalArgumentException("Failed to instantiate packet class ${P::class}")

        // Настраиваем пакет
        packet.setBuffer(buf)
        return packet
    }

    // Обработка пакета и логирование
    fun handle(data: T, context: ServerReceiverContext) {
        IhLogger.log("${context.player.name.string} --> <<$messageId>> (${data})", debugMode = true)
    }

    // Объект контекста, передаваемый в callback
    data class ServerReceiverContext(
        val server: MinecraftServer,
        val player: ServerPlayerEntity,
        val handler: ServerPlayPacketListener,
        val responseSender: PacketSender
    )
}
