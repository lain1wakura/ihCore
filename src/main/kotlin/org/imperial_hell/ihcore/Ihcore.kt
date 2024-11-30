package org.imperial_hell.ihcore

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.imperial_hell.ihcore.Characters.System.Commands.SyncCommand
import org.imperial_hell.ihcore.Characters.System.CharacterManager
import org.imperial_hell.ihcore.Characters.System.CharacterService
import org.imperial_hell.ihcore.Characters.System.UserManager
import org.imperial_hell.ihcore.Characters.System.UserService
import org.imperial_hell.ihcore.Files.DatabaseManager
import org.imperial_hell.ihcore.NetworkCore.PlayerHandler
import org.imperial_hell.ihcore.Sync.PlayerDataStorage
import org.imperial_hell.ihcore.server.ServerNetworkHandler

class Ihcore : ModInitializer {

    lateinit var databaseManager: DatabaseManager
    lateinit var playerManager : CharacterManager
    lateinit var playerHandler: PlayerHandler
    lateinit var userService: UserService
    lateinit var userManager: UserManager
    lateinit var characterService: CharacterService
    lateinit var playerDataStorage : PlayerDataStorage
    override fun onInitialize() {
        databaseManager = DatabaseManager("mongodb://localhost:27017/", "ihIntegration")
        userService = UserService(databaseManager)
        playerManager = CharacterManager(this)
        userManager = UserManager(this)
        characterService = CharacterService(databaseManager)
        databaseManager.connect()
        ServerNetworkHandler(this).registerServer()

        playerHandler = PlayerHandler(this)
        playerHandler.registerEvents()

        val syncCommand = SyncCommand(this)

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            syncCommand.register(dispatcher)
        }

        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping { server ->
            playerManager.saveAllCharacters()
        })

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            playerDataStorage = PlayerDataStorage(server)
        }

    }
}
