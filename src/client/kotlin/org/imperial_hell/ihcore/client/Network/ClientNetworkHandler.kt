package org.imperial_hell.ihcore.client.Network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.command.argument.EntityArgumentType.player
import net.minecraft.command.argument.UuidArgumentType.uuid
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.world.World
import org.imperial_hell.ihcore.NetworkCore.Packets.IhPacket
import org.imperial_hell.ihcore.NetworkCore.Packets.StringPacket
import org.imperial_hell.ihcore.NetworkCore.PacketsList
import org.imperial_hell.ihcore.client.IhcoreClient
import org.imperial_hell.ihcore.client.Messages.TypingMessageManager
import java.util.UUID
import java.util.concurrent.CompletableFuture

object ClientNetworkHandler {


    // Регистрация обработчиков пакетов на клиенте
    fun registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(PacketsList.SYNC_REQUEST) { client, handler, buf, sender ->
            //dsd
        }
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