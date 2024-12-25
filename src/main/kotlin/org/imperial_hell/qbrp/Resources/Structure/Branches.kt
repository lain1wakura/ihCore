package org.imperial_hell.qbrp.Resources.Structure

import org.imperial_hell.qbrp.Resources.UnitKey

enum class Branches(val key: UnitKey) {

    MODELS_REGISTRY(UnitKey("models")),
    TEXTURES_REGISTRY(UnitKey("textures")),
    ITEM_MODELS(UnitKey("items")),
    ITEM_TEXTURES(UnitKey("items_textures")),
    UNSET(UnitKey("unset")),
    ROOT(UnitKey("root"));

}