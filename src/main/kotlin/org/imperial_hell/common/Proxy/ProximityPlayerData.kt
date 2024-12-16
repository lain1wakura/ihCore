package org.imperial_hell.common.Proxy

import net.minecraft.util.Identifier
import org.imperial_hell.common.Utils.Animation
import org.imperial_hell.common.Utils.Icon

data class ProximityPlayerData(
    val playerUuid: String,
    var state: State,
    val messageTypingProgress: String,
    val characterUuid: String,
    val appearanceDesc: String,
) {

    lateinit var icon: Icon
    lateinit var iconAnimation: Animation

    // Метод для расчета иконки
    private fun calculateIcon(): Icon {
        return when (state) {
            State.NONE -> {
                if (!::icon.isInitialized) {
                    icon = Icon("default", Identifier("qbrp", "default_icon")) // Устанавливаем значение по умолчанию
                }
                icon // Возвращаем текущее значение
            }
            State.AFK -> Icon("afk", Identifier("qbrp", "afk"))
            State.TYPING_REPLICA -> Icon("nothing", Identifier("qbrp", ""))
            State.TYPING_COMMAND -> Icon("nothing", Identifier("qbrp", ""))
            State.TYPING_NRP -> Icon("nothing", Identifier("qbrp", ""))
        }
    }

    // При изменении состояния или других данных обновляем `icon`
    fun clientHandle() {
        icon = calculateIcon()
        iconAnimation = Animation(
            duration = 100.0,
            animationSpeed = 1f,
            mode = Animation.Mode.ONCE,
            progressMode = Animation.ProgressMode.EASE_IN_OUT
        )
    }

    enum class State {
        AFK,
        TYPING_REPLICA,
        TYPING_COMMAND,
        TYPING_NRP,
        NONE
    }

    companion object {
        fun getBlankPlayerData(playerUuid: String): ProximityPlayerData {
            return ProximityPlayerData(
                playerUuid = playerUuid,
                state = State.NONE,
                characterUuid = "",
                appearanceDesc = "",
                messageTypingProgress = ""
            )
        }
    }
}

