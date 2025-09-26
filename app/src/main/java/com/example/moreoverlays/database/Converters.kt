package com.example.moreoverlays.database

import androidx.room.TypeConverter
import com.example.moreoverlays.ContentTypeData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json {
        encodeDefaults = true
        prettyPrint = true
        classDiscriminator = "type" // обязательно, чтобы понимать, какой класс десериализовать
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun fromContentTypeList(value: List<ContentTypeData>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toContentTypeList(value: String): List<ContentTypeData> {
        return json.decodeFromString(value)
    }


}