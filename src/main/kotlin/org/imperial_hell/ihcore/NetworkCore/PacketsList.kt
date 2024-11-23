package org.imperial_hell.ihcore.NetworkCore
import net.minecraft.util.Identifier

object PacketsList {
    val MOD_ID = "ih_core"

    val GIVE_ALL_CHARACTERS = Identifier(MOD_ID, "give_all_characters") // Возвращает таблицу всех примененных персонажей текущих игроков на сервере (RQ) (Signal, Characters)

    val EXECUTE_COMMAND = Identifier(MOD_ID, "execute_command") // Заставляет выполнить команду (String)

    val SYNC_REQUEST = Identifier(MOD_ID, "sync_request") // Запрашивает у клиента ключ для синхронизации (Signal, String)
    val CHAT_TYPING = Identifier(MOD_ID, "chat_typing") // Отправляется на сервер, когда игрок нажимает на любую клавишу с открытым чатом (Signal, String)
    val END_TYPING = Identifier(MOD_ID, "end_typing") // Отправляется на сервер, когда игрок закрывает чат (Signal, String)

}