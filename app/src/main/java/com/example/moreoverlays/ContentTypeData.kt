package com.example.moreoverlays

import android.os.Parcelable
import com.example.moreoverlays.database.AppData
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Parcelize
@Serializable
sealed class ContentTypeData : Parcelable {
    abstract val type: String
}

@Parcelize
@Serializable
@SerialName("apps")
data class Apps(
    val id: Int,
    val apps: List<AppData>,
    override val type: String = "apps" // <---- REMEMBER ALL THIS LINES || I HATE CHAT GPT
) : ContentTypeData()

@Parcelize
@Serializable
@SerialName("widgets")
data class Widgets(
    val id: Int,
    override val type: String = "widgets" // <---- REMEMBER ALL THIS LINES || I HATE CHAT GPT
) : ContentTypeData()

@Parcelize
@Serializable
@SerialName("notes")
data class Notes(
    val id: Int,
    override val type: String = "notes" // <---- REMEMBER ALL THIS LINES || I HATE CHAT GPT
) : ContentTypeData()
