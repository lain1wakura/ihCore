package org.imperial_hell.ihcore.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

import org.imperial_hell.ihcore.client.Messages.TypingMessageManager
import org.imperial_hell.ihcore.client.Network.ClientNetworkHandler

class IhcoreClient : ClientModInitializer {

    var chatFlag = false

    override fun onInitializeClient() {
        ClientNetworkHandler.registerClient()
        typingMessageManager.registerTypingEvents()

        // Регистрация события ClientTick, которое будет срабатывать на каждом тике
        var screenOpened = false
         ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (client.world != null && !screenOpened) {
                // Проверка, что мир загружен и экран еще не был открыт
                screenOpened = true
                //MinecraftClient.getInstance().setScreen(SyncMenu())  // Открытие экрана
//                client.player?.sendMessage(Text.of("Пакет отправлен"), false) // Отправляем сообщение игроку
//                val response = responseRequest(
//                    packetId = PacketsList.GIVE_ALL_CHARACTERS,
//                    requestPacket = SignalPacket(),
//                    responseClass = CharactersPacket::class.java
//                )
//                client.player?.sendMessage(Text.of("Пакет получен: ${response}"), false) // Отправляем сообщение игроку
            }
        }

    }

    companion object {
        var typingMessageManager: TypingMessageManager = TypingMessageManager()
    }
}
