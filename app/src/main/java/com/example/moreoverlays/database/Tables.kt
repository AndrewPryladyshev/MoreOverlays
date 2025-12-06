package com.example.moreoverlays.database


import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moreoverlays.ContentTypeData
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
@Entity(tableName = "apps")
data class AppData(
    @PrimaryKey @ColumnInfo(name = "packages") val appPackage: String,
    @ColumnInfo(name = "appsNames") val appName: String,
    @ColumnInfo(name = "is_selected") var isSelected: Boolean = false,
) : Parcelable


@Entity(tableName = "widgets")
data class Widgets(
    @PrimaryKey val id: Int,
    // TODO: FINISH DATABASE AFTER APPS WILL FULLY WORK
)

@Serializable
@Parcelize
@Entity(tableName = "overlay_configs")
data class OverlayConfig(
    @PrimaryKey
    val id: Int,
    val name: String,
    val width: Int,
    val height: Int,
    val x: Int,
    val y: Int,
    var contentTypes: List<ContentTypeData>,
    var side: Int,
//    var currentItemId: Int,
) : Parcelable
{
    fun createOverlay(context: Context) : ViewGroup {
        val overlay = FrameLayout(context)
        overlay.id = id
        overlay.setBackgroundColor(Color.WHITE)
        return overlay
    }
}

