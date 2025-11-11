package com.ismaindra.alquranapp.data.adapter


import com.google.gson.*
import com.ismaindra.alquranapp.data.model.SurahInfo
import java.lang.reflect.Type

class SurahInfoAdapter : JsonDeserializer<SurahInfo?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SurahInfo? {
        if (json == null || json.isJsonNull) return null
        // kalau API kirim "false", abaikan (jadikan null)
        if (json.isJsonPrimitive && json.asJsonPrimitive.isBoolean) return null
        // kalau object, parse seperti biasa
        return context?.deserialize(json, SurahInfo::class.java)
    }
}