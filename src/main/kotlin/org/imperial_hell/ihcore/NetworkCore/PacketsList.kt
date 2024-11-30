package org.imperial_hell.ihcore.NetworkCore
import net.minecraft.util.Identifier

object PacketsList {
    val MOD_ID = "ih_core"

    val SYNC_REQUEST = Identifier(MOD_ID, "sync_request") // Запрашивает у клиента ключ для синхронизации (Signal, String)

    val CHAT_TYPING = Identifier(MOD_ID, "chat_typing") // Отправляется на сервер, когда игрок нажимает на любую клавишу с открытым чатом (Signal, String)
    val END_TYPING = Identifier(MOD_ID, "end_typing") // Отправляется на сервер, когда игрок закрывает чат (Signal, String)

    val PLAYER_DATA = Identifier(MOD_ID, "player_data") // Отправляется на сервер, когда игрок закрывает чат (Signal, String)
    val PLAYER_DATA_REQUEST = Identifier(MOD_ID, "player_data_request") // Отправляется на сервер, когда игрок закрывает чат (Signal, String)

}