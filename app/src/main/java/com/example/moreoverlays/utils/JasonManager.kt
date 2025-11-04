package com.example.moreoverlays.utils

import com.example.moreoverlays.Apps
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.Notes
import com.example.moreoverlays.Widgets
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object JsonManager {
    private val module = SerializersModule {
        polymorphic(ContentTypeData::class) {
            subclass(Apps::class)
            subclass(Widgets::class)
            subclass(Notes::class)
        }
    }

    val jsonConfigured = Json {
        encodeDefaults = true
        prettyPrint = false
        classDiscriminator = "type"
        ignoreUnknownKeys = true
        serializersModule = module
    }
}