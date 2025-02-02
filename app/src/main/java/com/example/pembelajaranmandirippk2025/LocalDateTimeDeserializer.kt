package com.example.pembelajaranmandirippk2025

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    override fun deserialize(json: JsonElement, typeOfT: java.lang.reflect.Type, context: com.google.gson.JsonDeserializationContext): LocalDateTime {
        return try {
            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            // Handle exception or provide default value
            LocalDateTime.now()  // Contoh fallback jika format salah
        }
    }
}

