package org.imperial_hell.ihcore.Files

import org.bson.Document
import org.imperial_hell.ihcore.Model.Character
import org.imperial_hell.ihcore.Model.Character.Appearance
import org.imperial_hell.ihcore.Model.PlayerStyle

object MongoConverter {

    /**
     * Преобразует документ MongoDB в объект Character.
     */
    fun mapDocumentToCharacter(document: Document): Character {
        val uuid = document.getString("uuid")
        val name = document.getString("name")
        val playerName = document.getString("author") ?: ""
        val nicknameColor = document.getInteger("hex_color")?.toString(16) ?: "FFFFFF" // Цвет в формате HEX
        val description = document.getString("desc") ?: ""

        val appearanceDoc = document.get("appearance") as? Document
        val appearance = appearanceDoc?.let { mapDocumentToAppearance(it) } ?: Appearance(
            description = null,
            skinModel = "classic",
            skinUrl = "",
            styles = mutableListOf(),
            style = 0
        )

        return Character(
            uuid = uuid,
            name = name,
            author = playerName,
            nicknameColor = nicknameColor,
            description = description,
            appearance = appearance
        )
    }

    /**
     * Преобразует документ MongoDB в объект Appearance.
     */
    fun mapDocumentToAppearance(document: Document): Appearance {
        val description = document.getString("desc")
        val skinModel = document.getString("model")
        val skinUrl = document.getString("skin_url") ?: ""

        // Обработка списка стилей
        val styles = (document.get("styles") as? List<*>)
            ?.filterIsInstance<String>()
            ?.toMutableList()
            ?: mutableListOf()

        val style = document.getInteger("style", 0)

        return Appearance(
            description = description,
            skinModel = skinModel,
            skinUrl = skinUrl,
            styles = styles,
            style = style
        )
    }

    /**
     * Преобразует объект Character в документ MongoDB.
     */
    fun mapCharacterToDocument(character: Character): Document {
        val appearanceDoc = mapAppearanceToDocument(character.appearance)

        return Document("uuid", character.uuid)
            .append("name", character.name)
            .append("author", character.author)
            .append("hex_color", character.nicknameColor?.toInt(16))
            .append("desc", character.description)
            .append("appearance", appearanceDoc)
    }

    /**
     * Преобразует объект Appearance в документ MongoDB.
     */
    fun mapAppearanceToDocument(appearance: Appearance): Document {
        return Document("model", appearance.skinModel)
            .append("desc", appearance.description)
            .append("skin_url", appearance.skinUrl)
            .append("styles", appearance.styles.toList()) // Сериализуем список стилей
            .append("style", appearance.style)
    }

    /**
     * Преобразует документ MongoDB в объект PlayerStyle.
     */
    fun mapDocumentToPlayerStyle(document: Document): PlayerStyle {
        val name = document.getString("name") ?: "Unnamed"
        val desc = document.getString("desc")
        val descType = document.getString("descType") ?: "append"
        val openTime = document.getInteger("openTime", 0)

        return PlayerStyle(
            name = name,
            desc = desc,
            descType = descType,
            openTime = openTime,
            top = (document.get("top") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf(),
            bottom = (document.get("bottom") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf(),
            fullCover = (document.get("fullCover") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf(),
            footwear = (document.get("footwear") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf(),
            accessories = (document.get("accessories") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf(),
            headwear = (document.get("headwear") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf(),
            skinUrl = document.getString("skinUrl")
        )
    }

    /**
     * Преобразует объект PlayerStyle в документ MongoDB.
     */
    fun mapPlayerStyleToDocument(playerStyle: PlayerStyle): Document {
        return Document("name", playerStyle.name)
            .append("desc", playerStyle.desc)
            .append("descType", playerStyle.descType)
            .append("openTime", playerStyle.openTime)
            .append("top", playerStyle.top.toList())
            .append("bottom", playerStyle.bottom.toList())
            .append("fullCover", playerStyle.fullCover.toList())
            .append("footwear", playerStyle.footwear.toList())
            .append("accessories", playerStyle.accessories.toList())
            .append("headwear", playerStyle.headwear.toList())
            .append("skinUrl", playerStyle.skinUrl)
    }
}
