package com.example.moreoverlays

import android.os.Parcelable
import com.example.moreoverlays.database.AppData
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Parcelize
@Serializable
sealed class ContentTypeData : Parcelable {
    abstract val id: Int
    abstract val title: String
    abstract val contentType: String

    fun getId() {
        return
    }
}

@Parcelize
@Serializable
@SerialName("apps")
@Polymorphic
data class Apps(
    override val id: Int,
    val apps: MutableList<AppData>,
    override val title: String = "",
    override val contentType: String = "Apps",
//    override val type: String = "apps" // <---- REMEMBER ALL THIS LINES || I HATE CHAT GPT
) : ContentTypeData()

@Parcelize
@Serializable
@SerialName("widgets")
@Polymorphic
data class Widgets(
    override val id: Int,
    override val title: String = "",
    override val contentType: String = "Widgets",
//    override val type: String = "widgets" // <---- REMEMBER ALL THIS LINES || I HATE CHAT GPT
) : ContentTypeData()

@Parcelize
@Serializable
@SerialName("notes")
@Polymorphic
data class Notes(
    override val id: Int,
    override val title: String = "",
    override val contentType: String = "Notes",
//    override val type: String = "notes" // <---- REMEMBER ALL THIS LINES || I HATE CHAT GPT
) : ContentTypeData()
