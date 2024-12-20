package org.imperial_hell.common.Packets

import com.google.gson.Gson
import kotlinx.serialization.json.JsonObject
import net.minecraft.network.PacketByteBuf

class JsonPacket(val json: String) : IhPacket() {
    override fun apply() {
    }

    override fun write() {
        buf.writeString(json)
        super.write()
    }

    override fun readHandle(buffer: PacketByteBuf): Any {
        val convertedObject : JsonObject = Gson().fromJson(buffer.readString(), JsonObject::class.java)
        return convertedObject
    }
}