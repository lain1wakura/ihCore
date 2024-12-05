package org.imperial_hell.ihcore.client.Network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.imperial_hell.ihcore.Networking.Packets.IhPacket
import org.imperial_hell.ihcore.Networking.PacketsList
import java.util.concurrent.CompletableFuture

object ClientNetworkHandler {


    // Регистрация обработчиков пакетов на клиенте
    fun registerClient() {
    }

    // Метод для отправки запроса и ожидания ответа
    fun responseRequest(packetId: Identifier, requestPacket: IhPacket, responseClass: Class<*>): Any? {
        val future = CompletableFuture<Any?>()

        try {
            ClientPacketSender.send(packetId, requestPacket)
            ClientPlayNetworking.registerGlobalReceiver(packetId) { _, _, buf, _ ->
                try {
                    (responseClass.getConstructor().newInstance() as? IhPacket)?.let {
                        it.setBuffer(buf)
                        future.complete(it.read())
                    } ?: future.completeExceptionally(IllegalArgumentException("Неверный тип пакета"))
                } catch (e: Exception) {
                    future.completeExceptionally(e)
                }
            }
            return future.join()
        } catch (e: Exception) {
            future.completeExceptionally(e)
            throw e
        }
    }


}