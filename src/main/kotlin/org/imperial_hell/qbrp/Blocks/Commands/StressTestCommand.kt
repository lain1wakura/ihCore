package org.imperial_hell.qbrp.Commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import org.imperial_hell.qbrp.Blocks.BlockDataManager
import org.imperial_hell.qbrp.Blocks.qbBlock
import org.imperial_hell.ihSystems.IhLogger
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StressTestCommand(private val blockDataManager: BlockDataManager) {

    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            net.minecraft.server.command.CommandManager.literal("blockStressTest") // Specify the command name
                .then(
                    net.minecraft.server.command.CommandManager.argument("x", IntegerArgumentType.integer(0)) // Argument for X-coordinate
                        .then(
                            net.minecraft.server.command.CommandManager.argument("y", IntegerArgumentType.integer(0)) // Argument for Y-coordinate
                                .then(
                                    net.minecraft.server.command.CommandManager.argument("z", IntegerArgumentType.integer(0)) // Argument for Z-coordinate
                                        .then(
                                            net.minecraft.server.command.CommandManager.argument("c", IntegerArgumentType.integer(0)) // Argument for chance
                                                .executes { context ->
                                                    val x = IntegerArgumentType.getInteger(context, "x")
                                                    val y = IntegerArgumentType.getInteger(context, "y")
                                                    val z = IntegerArgumentType.getInteger(context, "z")
                                                    val c = IntegerArgumentType.getInteger(context, "c")
                                                    val player = context.source.player!!

                                                    val px = player.x
                                                    val py = player.y
                                                    val pz = player.z

                                                    stressTestAsync(x, y, z, px, py, pz, c)
                                                    1 // Return a successful execution code
                                                }
                                        )
                                )
                        )
                )
        )

        dispatcher.register(
            net.minecraft.server.command.CommandManager.literal("blockCount") // Specify the command name
            .executes { context ->
                printData()
                1
            }
        )
    }

    fun printData() {
        IhLogger.log("Количество блоков: <<${blockDataManager.blocks.size}>>")
    }

    private fun stressTestAsync(x: Int, y: Int, z: Int, px: Double, py: Double, pz: Double, chance: Int) {
        CompletableFuture.runAsync({
            IhLogger.log("Стресс-тест: создание данных блоков на координатах <<($px, $py, $pz)>> с шансом $chance%", IhLogger.MessageType.INFO, debugMode = true)

            for (bX in -x..x) {
                for (bY in -y..y) {
                    for (bZ in -z..z) {
                        if (Math.random() * 100 < chance) {
                            val pos = BlockPos((px + bX).toInt(), (py + bY).toInt(), (pz + bZ).toInt())
                            val newBlockData = qbBlock() // Create a new block data object

                            blockDataManager.registerBlock(pos, newBlockData) // Register the block data
                        }
                    }
                }
            }
        }, executorService).exceptionally { ex ->
            IhLogger.log("Ошибка в стресс-тесте: ${ex.message}", IhLogger.MessageType.ERROR)
            null
        }
    }
}
