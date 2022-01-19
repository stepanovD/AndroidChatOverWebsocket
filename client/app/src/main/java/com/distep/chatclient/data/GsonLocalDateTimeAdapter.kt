package com.distep.chatclient.data

import com.google.gson.JsonSerializer
import com.google.gson.JsonDeserializer
import kotlin.jvm.Synchronized
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonDeserializationContext
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// this class can't be static
class GsonLocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @Synchronized
    override fun serialize(
        date: LocalDateTime,
        type: Type,
        jsonSerializationContext: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(date.format(DateTimeFormatter.ISO_DATE_TIME))
    }

    @Synchronized
    override fun deserialize(
        jsonElement: JsonElement,
        type: Type,
        jsonDeserializationContext: JsonDeserializationContext
    ): LocalDateTime {
        return LocalDateTime.parse(jsonElement.asString)
    }
}