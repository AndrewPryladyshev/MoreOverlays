package com.example.moreoverlays.database

import androidx.room.TypeConverter
import com.example.moreoverlays.Apps
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.Notes
import com.example.moreoverlays.Widgets
import com.example.moreoverlays.utils.JsonManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass



class Converters {
    private val json = JsonManager.jsonConfigured

    @TypeConverter
    fun fromContentTypeList(value: List<ContentTypeData>): String {
        return json.encodeToString(ListSerializer(ContentTypeData.serializer()), value)
    }

    @TypeConverter
    fun toContentTypeList(value: String): List<ContentTypeData> {
        return json.decodeFromString(ListSerializer(ContentTypeData.serializer()), value)
    }

}