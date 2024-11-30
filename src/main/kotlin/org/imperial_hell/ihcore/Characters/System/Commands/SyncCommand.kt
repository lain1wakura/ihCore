package org.imperial_hell.ihcore.Characters.System.Commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.imperial_hell.ihcore.Model.Character
import org.imperial_hell.ihcore.Ihcore

class SyncCommand(val server: Ihcore) {

    // Регистрация команды
    fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
    ) {
        // Здесь создается сама команда
        dispatcher.register(
            CommandManager.literal("ih_sync")
                .then(
                    CommandManager.argument("uuid", StringArgumentType.word())
                        .executes { context ->
                            execute(context)
                        }
                )
        )
    }

    // Метод для обработки команды
    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val uuid = StringArgumentType.getString(context, "uuid") // Получаем аргумент "name"
        val player = context.source.player as ServerPlayerEntity// Получаем игрока, который выполнил команд

        server.userManager.syncPlayer(player, uuid)

        return Command.SINGLE_SUCCESS
    }

}