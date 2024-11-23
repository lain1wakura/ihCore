package org.imperial_hell.ihcore.Skins

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.imperial_hell.ihcore.Skins.SkinManager.Model

object SkinCommand {

    // Регистрация команды
    fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
    ) {
        dispatcher.register(
            CommandManager.literal("ih_skin")
                .then(
                    CommandManager.argument("url", StringArgumentType.string())
                        .then(
                            CommandManager.argument("model", StringArgumentType.word())
                                .suggests { context, builder ->
                                    builder.suggest("classic")
                                    builder.suggest("slim")
                                    builder.buildFuture()
                                }
                                .executes { context -> execute(context) }
                        )
                        .executes { context -> executeWithDefaultModel(context) }
                )
        )
    }

    // Метод для обработки команды с обоими аргументами
    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val skinUrl = StringArgumentType.getString(context, "url") // Получаем URL скина
        val model = StringArgumentType.getString(context, "model")
        val player = context.source.player



        if (player is ServerPlayerEntity) {
            SkinManager.downloadSkin(skinUrl, player.name.string, Model.fromString(model) ?: Model.CLASSIC)

            return Command.SINGLE_SUCCESS
        }

        return Command.SINGLE_SUCCESS
    }

    // Метод для обработки команды с одним аргументом (model по умолчанию - CLASSIC)
    private fun executeWithDefaultModel(context: CommandContext<ServerCommandSource>): Int {
        val skinUrl = StringArgumentType.getString(context, "url") // Получаем URL скина
        val model = "CLASSIC" // Значение по умолчанию для моделиs
        val player = context.source.player

        if (player is ServerPlayerEntity) {
            SkinManager.downloadSkin(skinUrl, player.name.string, Model.fromString(model) ?: Model.CLASSIC)
            return Command.SINGLE_SUCCESS
        }

        return Command.SINGLE_SUCCESS
    }
}
