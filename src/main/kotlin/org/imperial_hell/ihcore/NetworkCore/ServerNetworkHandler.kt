package org.imperial_hell.ihcore.server

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import org.imperial_hell.ihcore.NetworkCore.CharactersPacket
import org.imperial_hell.ihcore.NetworkCore.Packets.StringPacket
import org.imperial_hell.ihcore.NetworkCore.PacketsList
import org.imperial_hell.ihcore.NetworkCore.ServerPacketSender
import org.imperial_hell.ihcore.Ihcore
import org.imperial_hell.ihcore.Characters.Character
import org.imperial_hell.ihcore.ChatTyping.ServerTypingBroadcaster

class ServerNetworkHandler(val server: Ihcore) {

    val databaseManager = server.databaseManager
    val playerManager = server.playerManager
    val jsonReader = server.jsonReader

    // Регистрация обработчиков пакетов на сервере
    fun registerServer() {

        // Обработка запроса синхронизации
        ServerPlayNetworking.registerGlobalReceiver(PacketsList.SYNC_REQUEST) { server, player, handler, buf, responseSender ->
            val packet = StringPacket()
            packet.setBuffer(buf)
            val uuid = packet.read()
            // Обработка пакета на сервере
            if (databaseManager.isUuidInDatabase(uuid) == true) {
                println(player.name.string)
                databaseManager.updateData("UPDATE minecraft_users SET nickname = ? WHERE uuid = ?", listOf(player.name.string, uuid))
                val lastCharacterUuid : String = databaseManager.getAllPlayerCharacters(uuid).last()["uuid"] as String
                val character = playerManager.createCharacterFromDatabase(lastCharacterUuid) as Character
                playerManager.applyCharacter(player, character)
            }
            //TODO("Исключить повторение кода здесь и в SyncCommand")
        }

        ServerPlayNetworking.registerGlobalReceiver(PacketsList.CHAT_TYPING) { server, player, handler, buf, responseSender ->
            ServerTypingBroadcaster.broadcastPlayerStartTyping(player)
        }
        ServerPlayNetworking.registerGlobalReceiver(PacketsList.END_TYPING) { server, player, handler, buf, responseSender ->
            ServerTypingBroadcaster.broadcastPlayerEndTyping(player)
        }

        //TODO("Доделать логику выборки онлайн-игроков")
        ServerPlayNetworking.registerGlobalReceiver(PacketsList.GIVE_ALL_CHARACTERS) { server, player, handler, buf, responseSender ->
            databaseManager.reload()
            val results = databaseManager.getAllFromTable("characters")
            val characters = CharactersPacket(results)
            characters.write()
            characters.apply()
            ServerPacketSender.send(player, PacketsList.GIVE_ALL_CHARACTERS, characters)
        }
    }
}

