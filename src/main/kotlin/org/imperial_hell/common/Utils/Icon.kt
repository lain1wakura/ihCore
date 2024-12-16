package org.imperial_hell.common.Utils

import net.minecraft.util.Identifier

class Icon(
    val name: String, // Уникальное имя иконки
    val texture: Identifier, // Текстура иконки
    val size: Float = 1.0f, // Размер иконки
    val opacity: Float = 1.0f // Прозрачность (от 0.0 до 1.0)
)
