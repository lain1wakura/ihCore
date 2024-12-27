package org.imperial_hell.qbrp.System

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource

interface ServerModCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>)
}