package org.imperial_hell.qbrp.client
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import org.imperial_hell.qbrp.Sync.IconManager
import org.imperial_hell.qbrp.Sync.ProximityDataManager
import org.imperial_hell.qbrp.Utils.qbTimer
import org.imperial_hell.common.Blocks.BlockData.Overlays.Overlay
import org.imperial_hell.qbrp.client.Game.OverlayManager
import org.imperial_hell.qbrp.client.Network.ClientNetworkHandler
import org.imperial_hell.qbrp.client.Sync.ResourceLoader

class qbrpClient : ClientModInitializer {

    override fun onInitializeClient() {
        ClientNetworkHandler.registerClient()
        ResourceLoader.downloadResources()

        var screenOpened = false
        ClientTickEvents.END_CLIENT_TICK.register { client ->

            // Убедимся, что игрок и мир доступны
            if (client.world != null && client.player != null) {
                if (!screenOpened) {
                    screenOpened = true
                    proximityDataManager = ProximityDataManager(client.player!!)
                    iconManager = IconManager(proximityDataManager)
                    proximityDataManager.registerReceiver()
                }
            }
        }

        var flag = true
        stressTest(10, 2, 10, -32.0, 93.0, -36.0, 2)
        WorldRenderEvents.LAST.register { context -> if (flag) { OverlayManager.renderOverlays(context) } }

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            dispatcher.register(
                literal<ServerCommandSource>("overlayTest") // Указываем тип источника команды
                    .then(
                        argument<ServerCommandSource, Int>("x", IntegerArgumentType.integer(0))
                            .then(
                                argument<ServerCommandSource, Int>("y", IntegerArgumentType.integer(0))
                                    .then(
                                        argument<ServerCommandSource, Int>("z", IntegerArgumentType.integer(0))
                                            .then(
                                                argument<ServerCommandSource, Int>("c", IntegerArgumentType.integer(0))
                                                    .executes { context ->
                                                        val x = IntegerArgumentType.getInteger(context, "x")
                                                        val y = IntegerArgumentType.getInteger(context, "y")
                                                        val z = IntegerArgumentType.getInteger(context, "z")
                                                        val c = IntegerArgumentType.getInteger(context, "c")
                                                        val player = context.source.player as PlayerEntity
                                                        val px = player.x
                                                        val py = player.y
                                                        val pz = player.z


                                                        stressTest(x, y, z, px, py, pz, c)
                                                        1
                                                    }
                                            )
                                    )
                            )
                    )
            )
        }

    }

    fun stressTest(x: Int, y: Int, z: Int, px: Double, py: Double, pz: Double, chance: Int) {
        OverlayManager.clear()
        for (bX in -x..x) {
            for (bY in -y..y) {
                for (bZ in -z..z) {
                    if (Math.random() * 100 < chance) {
                        OverlayManager.addOverlay(
                            BlockPos((px + bX).toInt(), (py + bY).toInt(), (pz + bZ).toInt()),
                            Overlay(Identifier("qbrp", "textures/items/item_placeholder.png"), "top")
                        )
                    }
                }
            }
        }

    }

    companion object {
        lateinit var proximityDataManager: ProximityDataManager
        lateinit var iconManager: IconManager
    }
}
