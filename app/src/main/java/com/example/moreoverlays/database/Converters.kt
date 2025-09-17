package com.example.moreoverlays.database

import androidx.room.TypeConverter
import com.example.moreoverlays.ContentTypeData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromList(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<Int> {
        return if (data.isEmpty()) emptyList()
        else data.split(",").map { it.toInt() }
    }


//    @TypeConverter
//    fun fromContentTypes(content: List<ContentTypeData>): String {
//        return gson.toJson(content)
//    }
//
//    @TypeConverter
//    fun toContentTypes(data: String): List<ContentTypeData> {
//        val listType = TypeToken.getParameterized(List::class.java, ContentTypeData::class.java).type
//        return gson.fromJson(data, listType)
//    }
}