package org.imperial_hell.qbrp.Removed

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.imperial_hell.qbrp.Characters.System.UserManager
import java.util.function.Supplier

class SyncCommand(val userManager: UserManager) {

    // Регистрация команды
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        // Создаем команду "ih_sync"
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
        val uuid = StringArgumentType.getString(context, "uuid") // Получаем UUID из аргумента
        val player = context.source.player // Получаем игрока, который вызвал команду

        if (player == null) {
            context.source.sendError(Text.of("Команду можно выполнить только игроком!"))
            return Command.SINGLE_SUCCESS // Возвращаем успешный код, несмотря на ошибку
        }

        // Вызов метода синхронизации
        try {
            userManager.syncPlayer(player, uuid)
            context.source.sendFeedback(Text.of("Игрок успешно синхронизирован!") as Supplier<Text?>?, false)
        } catch (e: Exception) {
            context.source.sendError(Text.of("Ошибка при синхронизации игрока: ${e.message}"))
        }

        return Command.SINGLE_SUCCESS
    }
}
