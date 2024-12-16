package org.imperial_hell.common.Utils

object ConsoleColors {
    const val RESET = "\u001B[0m"
    const val BLACK = "\u001B[30m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"
    const val PURPLE = "\u001B[35m"
    const val CYAN = "\u001B[36m"
    const val WHITE = "\u001B[37m"
    const val ORANGE = "\u001B[38;5;214m"
    const val BOLD = "\u001B[1m"
    const val UNDERLINE = "\u001B[4m"
    const val BACKGROUND_RED = "\u001B[41m"
    const val BACKGROUND_GREEN = "\u001B[42m"
    const val BACKGROUND_BLUE = "\u001B[44m"

    fun bold(text: String, color: String): String {
        return "$BOLD$color$text$RESET"
    }

    fun bold(text: String): String {
        return "$BOLD$text$RESET"
    }
}
