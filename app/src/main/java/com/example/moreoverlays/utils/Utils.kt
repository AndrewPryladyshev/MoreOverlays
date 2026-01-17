package com.example.moreoverlays.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import com.example.moreoverlays.America
import com.example.moreoverlays.database.AppData


const val CATCHER_OVERLAY = -1
const val MAIN_OVERLAY_RIGHT = 0
const val MAIN_OVERLAY_LEFT = 1
const val DOWN_SWIPE_RIGHT_SIDE_OVERLAY = 2
const val LEFT_SWIPE_OVERLAY = 3
const val UP_SWIPE_RIGHT_SIDE_OVERLAY = 4
const val DOWN_SWIPE_LEFT_SIDE_OVERLAY = 5
const val RIGHT_SWIPE_OVERLAY = 6
const val UP_SWIPE_LEFT_SIDE_OVERLAY = 7

const val NOTHING = 0
const val RIGHT_SIDE = 1
const val LEFT_SIDE = 2

const val CONTENT_APPS = 1
const val CONTENT_WIDGETS = 2
const val CONTENT_PHOTOS = 3
const val CONTENT_NOTES = 4


const val MAIN = 1
const val DIAGONAL_UP = 2
const val STRAIGHT = 3
const val DIAGONAL_DOWN = 4


fun getInstalledApps(context: Context): List<AppData> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveInfos = pm.queryIntentActivities(intent, 0)

    return resolveInfos.map { info ->
        val appInfo = info.activityInfo.applicationInfo
        val name = pm.getApplicationLabel(appInfo).toString()
        AppData(appInfo.packageName, name)
    }
}

fun Context.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}


fun createAppData(list: List<AppData>, context: Context) : MutableList<America> {
    val americaList = mutableListOf<America>()
    list.forEach { item ->
        val america: America = America(item.appPackage, item.appName, context.packageManager.getApplicationIcon(item.appPackage).toBitmap())
        americaList.add(america)
    }
    return americaList
}


//fun createPhotoView(context: Context, item: OverlayItemConfig.Photo): View {
//    val imageView = ImageView(context)
//    imageView.layoutParams = FrameLayout.LayoutParams(
//        FrameLayout.LayoutParams.MATCH_PARENT,
//        FrameLayout.LayoutParams.MATCH_PARENT
//    )
//    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
//    imageView.setImageURI(Uri.parse(item.uriString))
//    return imageView
//}
