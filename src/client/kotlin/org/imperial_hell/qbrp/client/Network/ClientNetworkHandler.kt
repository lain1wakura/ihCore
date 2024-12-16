package org.imperial_hell.qbrp.client.Network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.util.Identifier
import org.imperial_hell.common.Packets.IhPacket
import org.imperial_hell.common.Packets.PlayerDataPacket
import org.imperial_hell.common.PacketsList
import org.imperial_hell.common.Proxy.ProximityPlayerData
import org.imperial_hell.qbrp.client.Sync.ResourceLoader
import java.util.concurrent.CompletableFuture

object ClientNetworkHandler {


    // Регистрация обработчиков пакетов на клиенте
    fun registerClient() {
        ClientReceiver<ProximityPlayerData>(PacketsList.LOADRES) { data, context ->
            ResourceLoader.downloadResources()
        }.register<PlayerDataPacket>()
    }

    // Метод для отправки запроса и ожидания ответа
    fun responseRequest(
        packetId: Identifier,
        requestPacket: IhPacket,
        responseClass: Class<*>,
        callback: (Any?) -> Unit
    ) {
        try {
            val future = CompletableFuture<Any?>()

            // Отправка пакета
            ClientPacketSender.send(packetId, requestPacket)

            // Обработка ответа
            ClientPlayNetworking.registerGlobalReceiver(packetId) { _, _, buf, _ ->
                try {
                    val response = responseClass.getConstructor().newInstance() as? IhPacket
                    if (response != null) {
                        response.setBuffer(buf)
                        future.complete(response.read())
                    } else {
                        future.completeExceptionally(IllegalArgumentException("Неверный тип пакета"))
                    }
                } catch (e: Exception) {
                    future.completeExceptionally(e)
                }
            }

            // Асинхронная обработка результата
            future.whenComplete { result, exception ->
                ClientPlayNetworking.unregisterGlobalReceiver(packetId) // Удаляем обработчик
                if (exception != null) {
                    callback(null)
                } else {
                    callback(result)
                }
            }
        } catch (e: Exception) {
            callback(null)
        }
    }



}