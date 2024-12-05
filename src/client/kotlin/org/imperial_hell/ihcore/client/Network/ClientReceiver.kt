package org.imperial_hell.ihcore.client.Network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.ihcore.Networking.Packets.IhPacket

class ClientReceiver<T>(
    val messageId: Identifier,
    val callback: (T, ClientReceiverContext) -> Unit
) {

    // Метод для регистрации глобального приемника пакетов
    inline fun <reified P : IhPacket> register() {
        ClientPlayNetworking.registerGlobalReceiver(messageId) { client, handler, buf, sender ->
            // Создаем пакет через рефлексию
            val packet = createPacket<P>(buf)

            // Извлекаем данные типа T
            val data = packet.read() as T

            // Создаем объект ReceiverContext
            val context = ClientReceiverContext(client, handler, sender)
            handle(data)
            callback(data, context) // Передаем данные и контекст в callback
        }
    }

    inline fun <reified P : IhPacket> createPacket(buf: net.minecraft.network.PacketByteBuf): P {
        // Находим подходящий конструктор
        val constructor = P::class.constructors.firstOrNull()
            ?: throw IllegalArgumentException("No constructor found for packet class ${P::class}")

        // Создаем экземпляр с использованием callBy (чтобы параметры с значениями по умолчанию работали)
        val packet = constructor.callBy(emptyMap()) as? P
            ?: throw IllegalArgumentException("Failed to instantiate packet class ${P::class}")

        packet.setBuffer(buf)
        return packet
    }

    // Обработка пакета и логирование
    fun handle(data: T) {
        IhLogger.log("--> <<$messageId>> (${data})", debugMode = true)
    }

    // Объект контекста, передаваемый в callback
    data class ClientReceiverContext(
        val client: MinecraftClient,
        val handler: ClientPlayNetworkHandler,
        val sender: PacketSender
    )
}
