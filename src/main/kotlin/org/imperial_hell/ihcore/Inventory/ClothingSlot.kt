package org.imperial_hell.ihcore.Inventory

import net.minecraft.item.ItemStack

// Класс отдельного слота одежды
data class ClothingSlot(
    var itemStack: ItemStack = ItemStack.EMPTY // Предмет, который находится в слоте
)