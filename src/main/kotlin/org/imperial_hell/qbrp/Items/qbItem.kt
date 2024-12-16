package org.imperial_hell.qbrp.client.Items

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

// Кастомный предмет
class qbItem(settings: Settings) : Item(settings) {

    // Начальная текстура
    private var currentTextureId: Identifier = Identifier("qbrp", "config/resources/")

    // Метод для смены текстуры
    fun changeTexture(newTexturePath: String) {
        this.currentTextureId = Identifier("qbitems", newTexturePath)
    }

    // Метод для получения текущей текстуры
    fun getTextureId(): Identifier {
        return currentTextureId
    }

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack?> {
        // Меняем текстуру при использовании предмета
        changeTexture("textures/item/player_head.png")

        // Обновляем модель предмета на клиентской стороне
        //updateModel(user)

        // Не забываем возвращать super.use для выполнения базового поведения предмета
        return super.use(world, user, hand)
    }

//    private fun updateModel(player: PlayerEntity?) {
//        val client = MinecraftClient.getInstance()
//        val stack = player?.mainHandStack ?: return
//
//        // Получаем ItemRenderer
//        val itemRenderer = client.itemRenderer
//
//        // Создаем новый ModelIdentifier
//        val modelId = Identifier("qbitems", "item/my_alternate_model")
//        val modelIdentifier = ModelIdentifier(modelId, "inventory")
//
//        // Привязываем новый ModelIdentifier к предмету
//        itemRenderer.models.putModel(this, modelIdentifier)
//    }
}
