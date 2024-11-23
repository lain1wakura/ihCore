package org.imperial_hell.ihcore

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.imperial_hell.ihcore.Characters.System.Commands.SyncCommand
import org.imperial_hell.ihcore.Characters.System.PlayerManager
import org.imperial_hell.ihcore.Characters.System.PlayerNameManager
import org.imperial_hell.ihcore.NetworkCore.PlayerHandler
import org.imperial_hell.ihcore.Files.DatabaseManager
import org.imperial_hell.ihcore.Files.JsonReader
import org.imperial_hell.ihcore.server.ServerNetworkHandler

class Ihcore : ModInitializer {

    lateinit var databaseManager: DatabaseManager
    lateinit var playerManager : PlayerManager
    lateinit var jsonReader: JsonReader
    lateinit var playerHandler: PlayerHandler


    override fun onInitialize() {
        databaseManager = DatabaseManager(this)
        playerManager = PlayerManager(this)
        jsonReader = JsonReader(this)
        databaseManager.connect()
        ServerNetworkHandler(this).registerServer()

        playerHandler = PlayerHandler(this)
        playerHandler.registerEvents()

        val syncCommand = SyncCommand(this)

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            syncCommand.register(dispatcher)
        }

        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping { server ->
            playerManager.saveAllPlayers()
        })

    }
}
