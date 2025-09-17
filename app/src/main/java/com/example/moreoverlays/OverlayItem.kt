package com.example.moreoverlays

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup


import kotlinx.parcelize.Parcelize

@Parcelize
class OverlayItem(
    val id: Int,
    val width: Int,
    val height: Int,
    val x: Int,
    val y: Int,
    val contentTypes: List<Int>,
) : Parcelable
{

}