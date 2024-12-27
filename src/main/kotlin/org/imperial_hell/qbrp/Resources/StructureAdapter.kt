package org.imperial_hell.qbrp.Resources

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.imperial_hell.qbrp.Resources.Structure.Branch
import org.imperial_hell.qbrp.Resources.Structure.Structure
import org.imperial_hell.qbrp.Resources.ContentUnits.ContentUnit
import org.imperial_hell.qbrp.Resources.UnitKey
import org.imperial_hell.qbrp.Resources.Data.RawData
import java.io.File
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.nio.file.Path
import java.nio.file.Paths

class DynamicPathAdapter : JsonSerializer<Path>, JsonDeserializer<Path> {
    override fun serialize(src: Path, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        //return JsonPrimitive(src.pathString)
        return JsonPrimitive("test")
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Path {
        return Paths.get(json.asString)
    }
}

// Сериализатор и десериализатор для File
class FileAdapter : JsonSerializer<File>, JsonDeserializer<File> {
    override fun serialize(src: File, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        //return JsonPrimitive(src.path)
        return JsonPrimitive("path")
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): File {
        println("eqewe")
        return File(json.asString)
    }
}