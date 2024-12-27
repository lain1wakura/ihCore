package org.imperial_hell.qbrp.API

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import org.imperial_hell.common.Packets.SignalPacket
import org.imperial_hell.common.PacketsList
import org.imperial_hell.qbrp.Networking.ServerPacketSender
import org.imperial_hell.qbrp.Resources.ResourceCentre

object qbApi {

    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                CommandManager.literal("qbapi")
                    .then(CommandManager.literal("res_bake").executes { context ->
                        //ResourceCentre.bakeResourcePack()
                        TODO("Доделать res_bake")
                        1
                })
                    .then(CommandManager.literal("res_load").executes { context ->
                        ServerPacketSender.send(context.source.player, PacketsList.LOADRES, SignalPacket())
                        1
                })
            )
        }

    }

}