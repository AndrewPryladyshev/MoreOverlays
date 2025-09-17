package com.example.moreoverlays

import android.os.Parcelable
import com.example.moreoverlays.database.AppData
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ContentTypeData : Parcelable {

    @Parcelize
    data class Apps(
        val id: Int,
        val apps: List<AppData>,
    ) : ContentTypeData()

    @Parcelize
    data class Widgets(
        val id: Int,
    ) : ContentTypeData()

    @Parcelize
    data class Notes(
        val id: Int,
    ) : ContentTypeData()
}