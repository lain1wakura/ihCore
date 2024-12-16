package org.imperial_hell.qbrp.Inventory

class SlotContainers() {
    // Типы контейнеров для слотов
    enum class SlotType {
        PANTS, // Карманы штанов
        HOODIE, // Карманы худи
        JACKET // Карманы куртки
    }

    // Контейнер для хранения нескольких слотов
    data class ClothingSlotContainer(
        val name: String, // Название контейнера (например, "П. штаны")
        val slotCount: Int, // Количество слотов в контейнере
        val type: SlotType, // Тип контейнера
        val slots: List<ClothingSlot> = List(slotCount) { ClothingSlot() } // Инициализация слотов
    )
}