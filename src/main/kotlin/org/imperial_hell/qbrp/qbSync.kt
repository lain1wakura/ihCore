package org.imperial_hell.qbrp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Game.Blocks.ChuckHandler
import org.imperial_hell.qbrp.Characters.System.CharacterManager
import org.imperial_hell.qbrp.Characters.System.UserManager
import org.imperial_hell.qbrp.Characters.System.UserService
import org.imperial_hell.qbrp.System.Files.DatabaseManager
import org.imperial_hell.qbrp.Networking.PlayerHandler
import org.imperial_hell.qbrp.Sync.HTTP.ResourceServer
import org.imperial_hell.qbrp.Sync.PlayerDataManager
import org.imperial_hell.common.Utils.ConsoleColors
import org.imperial_hell.common.Utils.TimerUpdater
import org.imperial_hell.qbrp.API.qbApi
import org.imperial_hell.qbrp.Game.GameContent
import org.imperial_hell.qbrp.Secrets.Databases
import org.imperial_hell.qbrp.server.ServerNetworkHandler
import org.imperial_hell.qbrp.Resources.ResourceCentre
import org.imperial_hell.qbrp.System.CommandsRepository

//import su.plo.voice.api.server.PlasmoVoiceServer

class qbSync : ModInitializer {

    lateinit var resourceServer: ResourceServer
    lateinit var chunkHandler: ChuckHandler
    lateinit var gameContent: GameContent

    val commandsRepository = CommandsRepository()

    override fun onInitialize() {
        // Запуск ResourceServer в корутине
        if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
            GlobalScope.launch {
                runResourceServer()
            }
            registerServerLifecycle()
        }
        registerContent()
        qbApi.register()
        TimerUpdater.registerCycle()
        printDataBlock()
    }

    private suspend fun runResourceServer() {
        withContext(Dispatchers.IO) {
            try {
                resourceServer = ResourceServer()
                resourceServer.init()
                IhLogger.log("ResourceServer успешно запущен на порту 8080.")
            } catch (e: Exception) {
                IhLogger.log("Ошибка запуска ResourceServer: ${e.message}", IhLogger.MessageType.ERROR)
            }
        }
    }

    fun registerContent() {
        gameContent = GameContent()
        ResourceCentre.bakeResourcePack(gameContent.items.baseItems)
    }


    fun registerHandlers() {
        //PlayerHandler(userManager).registerEvents()
        //ServerNetworkHandler(userManager).registerServer()
    }

    fun registerServerLifecycle() {
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping { server ->
            gameContent.blocks.manager.removeAll()
            //userManager.characterManager.saveAllCharacters()
            resourceServer.stop()
        })

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            init(server)
        }
    }

    fun init(server: MinecraftServer) {
        //val userService = UserService(characterDatabaseManager)
        chunkHandler = ChuckHandler(gameContent.blocks.manager)
        chunkHandler.register()
        //playerDataManager = PlayerDataManager(server)
        //userManager = UserManager(userService, CharacterManager(userService), playerDataManager)

        commandsRepository.initCommands(server.commandManager.dispatcher)
        registerHandlers()
    }

    fun printDataBlock() {
        val asciiArt = """
            
         _____  ______   ______  _____ 
        |   __| |_____] |_____/ |_____]
        |____\| |_____] |    \_ |             
                        
        """.trimIndent()

        asciiArt.lines().forEach { line ->
            IhLogger.log(ConsoleColors.bold("<<$line>>"))
        }

        if (gameContent.blocks.database.error == "") {
            IhLogger.log("<<|>> Подключение к базе данных ${gameContent.blocks.database.db?.name} успешно.")
        } else {
            IhLogger.log("| Ошибка при подключении к базе данных ${gameContent.blocks.database.db?.name}: ${gameContent.blocks.database.error}", IhLogger.MessageType.ERROR)
        }
        if (ResourceCentre.pack.isBaked()) {
            IhLogger.log("<<|>> Набор ресурсов инициализирован. Количество моделей: ${ResourceCentre.pack.structure.modelsRegistry.children.size}")
        } else {
            IhLogger.log("| Ошибка при инициализации набора ресурсов. Пакет не создан.", IhLogger.MessageType.ERROR)
        }
    }

    companion object {
        const val MOD_ID = "qbrp"
    }
}
