package com.example.moreoverlays.database


import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import android.view.View
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moreoverlays.ContentTypeData
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "apps")
data class AppData(
    @PrimaryKey @ColumnInfo(name = "packages") val appPackage: String,
    @ColumnInfo(name = "appsNames") val appName: String,
) : Parcelable


@Entity(tableName = "widgets")
data class Widgets(
    @PrimaryKey val id: Int,
    // TODO: FINISH DATABASE AFTER APPS WILL FULLY WORK
)

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
    val contentTypes: List<ContentTypeData>,
//    var currentItemId: Int,
) : Parcelable
{
    fun createOverlay(context: Context) : View {
        val overlay = View(context)
        overlay.id = id
        overlay.setBackgroundColor(Color.WHITE)
        return overlay
    }
}

