package org.imperial_hell.qbrp.Game.Items

import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.imperial_hell.qbrp.Resources.Structure.Parents
import org.imperial_hell.qbrp.client.Items.BaseItemType
import org.imperial_hell.qbrp.qbSync.Companion.MOD_ID
import server_common.ItemConfig
import java.io.File

class Items(itemsDirectory: File) {
    val itemsMap = ItemConfig.getTags(itemsDirectory)
    val baseItems = mutableListOf<BaseItem>()

    init {
        registerItems(listOf(
            BaseItem("custom_item_generated", modelType = Parents.GENERATED),
            BaseItem("custom_item_handheld", modelType = Parents.HANDHELD)
        ))
    }

    fun registerItems(item: List<BaseItem>) {
        item.forEach { registerItem(it) }
    }

    fun registerItem(item: BaseItem) {
        baseItems.add(item)
        Registry.register(Registries.ITEM, Identifier(MOD_ID, item.name), item.type)
    }
}