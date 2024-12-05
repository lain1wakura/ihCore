package org.imperial_hell.ihcore.Sync

data class ProximityPlayerData(
    val playerUuid: String,
    val state: State,
    val messageTypingProgress: String,
    val characterUuid: String,
    val appearanceDesc: String,
) {

    enum class State {
        AFK,
        TYPING_REPLICA,
        TYPING_COMMAND,
        TYPING_NRP,
        NONE
    }

    companion object {
        fun getBlankPlayerData(playerUuid: String): ProximityPlayerData {
            return ProximityPlayerData(playerUuid = playerUuid, state = State.NONE, characterUuid = "", appearanceDesc = "", messageTypingProgress = "")
        }
    }
}
