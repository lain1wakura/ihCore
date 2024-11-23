package org.imperial_hell.ihcore.client.Messages

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import org.imperial_hell.ihcore.NetworkCore.Packets.SignalPacket
import org.imperial_hell.ihcore.NetworkCore.PacketsList
import org.imperial_hell.ihcore.client.Network.ClientNetworkHandler
import org.imperial_hell.ihcore.client.Network.ClientPacketSender
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class TypingMessageManager {

    private var typingPlayers : MutableMap<UUID, AbstractClientPlayerEntity> = mutableMapOf()

    private data class HeadRotation(val yaw: Float, val pitch: Float)
    private val previousRotations = ConcurrentHashMap<PlayerEntity, HeadRotation>()
    private val typingStartTimes = ConcurrentHashMap<UUID, Long>()
    private val typingEndTimes = mutableMapOf<UUID, Long>()
    private var dotCycle = System.currentTimeMillis();

    var chatFlag = false

    // Метод для регистрации начала набора сообщения
    fun startTyping(playerUuid: UUID) {
        typingStartTimes[playerUuid] = System.currentTimeMillis()
    }

    fun getDotCycle(): Long {
        return dotCycle
    }

    fun updateDotCycle() {
        dotCycle = System.currentTimeMillis()
    }

    // Метод для завершения набора сообщения
    fun stopTyping(playerUuid: UUID) {
        typingStartTimes.remove(playerUuid)
    }

    // Устанавливаем время конца набора
    fun setEndTime(playerId: UUID) {
        typingEndTimes[playerId] = System.currentTimeMillis()
    }

    // Получение времени окончания набора (если оно существует)
    fun getEndTime(playerId: UUID): Long {
        return typingEndTimes[playerId] ?: 0L // Возвращаем 0, если игрок не закончил набор
    }

    // Метод для получения времени начала набора сообщения
    fun getStartTime(playerUuid: UUID): Long {
        return typingStartTimes[playerUuid] ?: System.currentTimeMillis()
    }

    fun updateTypingPlayers(players: List<AbstractClientPlayerEntity>) {
        typingPlayers.clear()
        for (player in players) {
            typingPlayers[player.uuid] = player
        }
    }

    fun addTypingPlayer(uuid: UUID) {
        val world: World = MinecraftClient.getInstance().world as World
        val player = world.getPlayers().firstOrNull { it.uuid == uuid } as AbstractClientPlayerEntity
        println("Добавлен игрок: ${player.uuid}. Размер массива: ${typingPlayers.size}, ${typingPlayers.values}")
        typingPlayers.put(uuid, player)
        startTyping(uuid)
    }

    fun removeTypingPlayer(uuid: UUID) {
        println("Убран игрок: ${uuid}")
        typingPlayers.remove(uuid)
        setEndTime(uuid)
        stopTyping(uuid)
    }

    fun isTyping(uuid: UUID): Boolean {
        //println("Обращение: ${uuid}, ${typingPlayers.containsKey(uuid)} | ${typingPlayers.size}, ${typingPlayers.keys}")
        return typingPlayers.containsKey(uuid)
    }

    fun registerTypingEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
            onClientTick(client)
        })
    }

    // Метод, вызываемый на каждом тике клиента
    private fun onClientTick(client: MinecraftClient) {

        if (chatFlag && !isChatOpen()) {
            ClientPacketSender.send(PacketsList.END_TYPING, SignalPacket())
            chatFlag = true
        } else {
            chatFlag = false
        }
    }

    private fun isChatOpen(): Boolean {
        val client = MinecraftClient.getInstance()
        // Проверяем, если текущее окно является экземпляром ChatScreen
        return client.currentScreen is ChatScreen
    }

    // Проверка изменения ориентации головы игрока
    private fun checkPlayerHeadRotation(player: PlayerEntity) {
        val previousRotation = previousRotations[player]
        val currentYaw = player.yaw
        val currentPitch = player.pitch

        // Если есть предыдущее значение ориентации, проверяем на изменения
        if (previousRotation != null) {
            if (previousRotation.yaw != currentYaw || previousRotation.pitch != currentPitch) {
                // Если ориентация изменилась, вызываем onPlayerRotate
                onPlayerRotate(player, previousRotation.yaw, previousRotation.pitch, currentYaw, currentPitch)
            }
        }

        // Обновляем последнюю известную ориентацию игрока
        previousRotations[player] = HeadRotation(currentYaw, currentPitch)
    }

    // Событие изменения ориентации головы игрока
    private fun onPlayerRotate(player: PlayerEntity, fromYaw: Float, fromPitch: Float, toYaw: Float, toPitch: Float) {
        if (isTyping(player.uuid)) {
            removeTypingPlayer(player.uuid)
        }
    }

}