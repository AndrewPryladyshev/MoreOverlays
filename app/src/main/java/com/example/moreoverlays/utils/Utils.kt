package com.example.moreoverlays.utils

import android.content.Context
import android.content.pm.PackageManager

const val CATCHER_OVERLAY = -1
const val MAIN_OVERLAY = 0
const val DOWN_RIGHT_SWIPE_OVERLAY = 1
const val LEFT_SWIPE_OVERLAY = 2
const val UP_RIGHT_SWIPE_OVERLAY = 3
const val DOWN_LEFT_SWIPE_OVERLAY = 4
const val RIGHT_SWIPE_OVERLAY = 5
const val UP_LEFT_SWIPE_OVERLAY = 6




fun getInstalledApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    return packages.mapNotNull {
        try {
            val name = pm.getApplicationLabel(it).toString()
            AppInfo(name, it.packageName)
        } catch (e: Exception) {
            null
        }
    }
}