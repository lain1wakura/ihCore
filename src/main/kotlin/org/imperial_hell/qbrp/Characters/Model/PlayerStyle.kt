package org.imperial_hell.qbrp.Characters.Model

data class PlayerStyle(
    val name: String,
    val desc: String?,
    val descType: String = "append",
    var openTime: Int = 0,
    val top: MutableList<String> = mutableListOf(),
    val bottom: MutableList<String> = mutableListOf(),
    val fullCover: MutableList<String> = mutableListOf(),
    val footwear: MutableList<String> = mutableListOf(),
    val accessories: MutableList<String> = mutableListOf(),
    val headwear: MutableList<String> = mutableListOf(),
    val skinUrl: String?
) {
    // Методы для управления элементами стиля
    fun addTopItem(item: String) {
        top.add(item)
    }

    fun addBottomItem(item: String) {
        bottom.add(item)
    }

    fun addFullCoverItem(item: String) {
        fullCover.add(item)
    }

    fun addFootwearItem(item: String) {
        footwear.add(item)
    }

    fun addAccessory(item: String) {
        accessories.add(item)
    }

    fun addHeadwear(item: String) {
        headwear.add(item)
    }
}