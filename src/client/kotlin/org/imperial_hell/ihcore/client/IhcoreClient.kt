package org.imperial_hell.ihcore.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import org.imperial_hell.ihcore.Sync.ProximityDataManager
import org.imperial_hell.ihcore.Utils.IhTimer

import org.imperial_hell.ihcore.client.Messages.TypingMessageManager
import org.imperial_hell.ihcore.client.Network.ClientNetworkHandler

class IhcoreClient : ClientModInitializer {

    var timer: IhTimer = IhTimer(20)

    lateinit var proximityDataManager: ProximityDataManager

    override fun onInitializeClient() {
        ClientNetworkHandler.registerClient()

        typingMessageManager.registerTypingEvents()

        // Регистрация события ClientTick, которое будет срабатывать на каждом тике
        var screenOpened = false
         ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (client.world != null && !screenOpened) {
                screenOpened = true

                proximityDataManager = ProximityDataManager(MinecraftClient.getInstance().player as ClientPlayerEntity)
                if (timer.hasReached()) {
                    // Ваш код, который нужно вызывать с указанным интервалом
                    proximityDataManager.updateProximityData()
                }

                proximityDataManager.registerReceiver()
            }
        }

    }

    companion object {
        var typingMessageManager: TypingMessageManager = TypingMessageManager()
    }
}
