package org.imperial_hell.qbrp.Resources.Data

import com.google.gson.annotations.SerializedName

// Класс, представляющий вложенный объект "pack"
data class Pack(
    @SerializedName("pack_format")
    val packFormat: Int, // Используем правильное имя поля, чтобы оно соответствовало JSON

    @SerializedName("description")
    val description: String
)

// Основной класс MetaData с вложенным объектом "pack"
data class MetaData(
    val pack: Pack // Вложенная структура
) : RawData()
