package org.imperial_hell.ihcore.Characters


class Character(
    val uuid: String,
    val name: String,
    val playerName: String,
    var nicknameColor: String?,
    var description: String?,
    val jsonPath: String,
    val syncedPlayer: String?,
    val appearance: Appearance
) {

    // Вложенный класс для внешнего вида персонажа
    data class Appearance(
        var mode: String,
        var description: String?,
        var skinModel: SkinModel,
        var skinUrl: String,
        val styles: MutableList<PlayerStyle> = mutableListOf(),
        var style: Int
    ) {
        // Enum для типа скина (slim или classic)
        enum class SkinModel {
            SLIM, CLASSIC
        }

        // Метод для добавления стиля к внешнему виду
        fun addStyle(style: PlayerStyle) {
            styles.add(style)
        }

        // Метод для поиска стиля по имени
        fun getStyleByName(name: String): PlayerStyle? {
            return styles.find { it.name == name }
        }

        fun getCurrentStyle(): PlayerStyle {
            return styles[style]
        }

        fun getCurrentStyleString(): String {
            if (styles.isNotEmpty()) {
                return styles[style].name
            } else {
                return "-"
            }
        }
    }

    /*    // Пример метода для обновления внешнего вида с базы данных
        fun updateAppearanceFromDatabase(databaseManager: DatabaseManager) {
            val newAppearanceData = databaseManager.getAppearanceData(uuid)
            if (newAppearanceData != null) {
                appearance.mode = newAppearanceData.mode
                appearance.description = newAppearanceData.description
                appearance.skinModel = Appearance.SkinModel.valueOf(newAppearanceData.skinModel.uppercase())
                appearance.skinUrl = newAppearanceData.skinUrl
                appearance.styles.clear()
                appearance.styles.addAll(newAppearanceData.styles)
            }
        }*/
}
