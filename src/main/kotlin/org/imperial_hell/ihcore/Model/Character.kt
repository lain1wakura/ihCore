package org.imperial_hell.ihcore.Model


data class Character(
    val uuid: String,
    val name: String,
    val author: String,
    var nicknameColor: String?,
    var description: String?,
    val appearance: Appearance
) {

    // Вложенный класс для внешнего вида персонажа
    data class Appearance(
        var description: String?,
        var skinModel: String,
        var skinUrl: String,
        val styles: MutableList<String> = mutableListOf(),
        var style: Int
    )
}
