package org.imperial_hell.qbrp

import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import org.imperial_hell.ihSystems.IhLogger
import org.imperial_hell.qbrp.Blocks.BlockDataManager
import org.imperial_hell.qbrp.Blocks.ChuckHandler
import org.imperial_hell.qbrp.Blocks.qbBlocksService
import org.imperial_hell.qbrp.Characters.System.CharacterManager
import org.imperial_hell.qbrp.Characters.System.UserManager
import org.imperial_hell.qbrp.Characters.System.UserService
import org.imperial_hell.qbrp.Commands.StressTestCommand
import org.imperial_hell.qbrp.Files.DatabaseManager
import org.imperial_hell.qbrp.Files.IhConfig
import org.imperial_hell.qbrp.Networking.PlayerHandler
import org.imperial_hell.qbrp.Sync.HTTP.ResourceServer
import org.imperial_hell.qbrp.Sync.PlayerDataManager
import org.imperial_hell.qbrp.Sync.ResourceInstruments
import org.imperial_hell.qbrp.Sync.ResourcePackBaker
import org.imperial_hell.common.Utils.ConsoleColors
import org.imperial_hell.common.Utils.TimerUpdater
import org.imperial_hell.qbrp.client.Items.qbItem
import org.imperial_hell.qbrp.server.ServerNetworkHandler

class qbSync : ModInitializer {

    lateinit var blockDatabaseManager: DatabaseManager
    lateinit var characterDatabaseManager: DatabaseManager
    lateinit var userManager: UserManager
    lateinit var playerDataManager: PlayerDataManager
    lateinit var blockDataManager: BlockDataManager
    lateinit var resourceServer: ResourceServer

    var connectionState = false
    var itemsState = ""

    override fun onInitialize() {
        // Запуск ResourceServer в корутине
        if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {

            GlobalScope.launch {
                runResourceServer()
            }

            ResourcePackBaker.process(IhConfig.SERVER_PACK_CONTENT_PATH, IhConfig.SERVER_ITEM)
            initDatabase()
            registerServerLifecycle()
        }
        registerQbItem()
        TimerUpdater.registerCycle()
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

    fun initDatabase() {
        blockDatabaseManager = DatabaseManager("mongodb://root:r1RdLdFhIpAmvZk9@5.9.189.53:27017/", "qbblocks")
        connectionState = blockDatabaseManager.connect()
        characterDatabaseManager = DatabaseManager("mongodb://root:r1RdLdFhIpAmvZk9@5.9.189.53:27017/", "qbblocks")
    }

    fun registerQbItem() {
        Registry.register(Registries.ITEM, Identifier(MOD_ID, "custom_item_handheld"), CUSTOM_ITEM_HANDHELD)
        Registry.register(Registries.ITEM, Identifier(MOD_ID, "custom_item_generated"), CUSTOM_ITEM_GENERATED)
    }

    fun registerCommands(dispatcher: CommandDispatcher<ServerCommandSource>) {
        //SyncCommand(userManager).register(dispatcher)
        StressTestCommand(blockDataManager).register(dispatcher)
        ResourceInstruments().register(dispatcher)
    }

    fun registerHandlers() {
        ChuckHandler(blockDataManager).register()
        PlayerHandler(userManager).registerEvents()
        ServerNetworkHandler(userManager).registerServer()
    }

    fun registerServerLifecycle() {
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvents.ServerStopping { server ->
            blockDataManager.removeAll()
            userManager.characterManager.saveAllCharacters()
            resourceServer.stop()
        })

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            init(server)
        }
    }

    fun init(server: MinecraftServer) {
        val userService = UserService(characterDatabaseManager)
        playerDataManager = PlayerDataManager(server)
        userManager = UserManager(userService, CharacterManager(userService), playerDataManager)
        blockDataManager = BlockDataManager(qbBlocksService(blockDatabaseManager))

        registerCommands(server.commandManager.dispatcher)
        registerHandlers()
        printDataBlock()
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

        if (connectionState) {
            IhLogger.log("<<|>> Подключение к базе данных ${blockDatabaseManager.db?.name} успешно.")
        } else {
            IhLogger.log("| Ошибка при подключении к базе данных ${blockDatabaseManager.db?.name}: ${blockDatabaseManager.error}", IhLogger.MessageType.ERROR)
        }
        if (itemsState == "") {
            IhLogger.log("<<|>> Набор ресурсов инициализирован. Количество моделей: ${ResourcePackBaker.models_count}")
        } else {
            IhLogger.log("| Ошибка при инициализации набора ресурсов: $itemsState", IhLogger.MessageType.ERROR)
        }
    }

    companion object {
        const val MOD_ID = "qbrp"
        var CUSTOM_ITEM_HANDHELD = qbItem(Item.Settings())
        var CUSTOM_ITEM_GENERATED = qbItem(Item.Settings())
    }
}
