package org.imperial_hell.qbrp.Game.Items

import net.minecraft.item.Item
import net.minecraft.util.Identifier
import org.imperial_hell.qbrp.Resources.Structure.Parents
import org.imperial_hell.qbrp.client.Items.BaseItemType
import org.imperial_hell.qbrp.qbSync.Companion.MOD_ID

class BaseItem(val name: String,
               val type: Item = BaseItemType(Item.Settings()),
               val modelType: Parents = Parents.GENERATED
) {
    val identifier = Identifier(MOD_ID, name)
}