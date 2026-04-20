package com.example.moreoverlays.services

import android.graphics.Color
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.America
import com.example.moreoverlays.R
import com.example.moreoverlays.adapters.AppsListAdapter
import com.example.moreoverlays.adapters.AppsListAdapter.AppsViewHolder
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.utils.AppsDiffCallback
import com.example.moreoverlays.utils.createAppData

class AccessibilityAppsListAdapter(private val onItemClicked: (AppData) -> Unit) : ListAdapter<America, AccessibilityAppsListAdapter.ViewHolder>(AppsDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.iv_app_icon)
    }
/*
 * Copyright (c) 2026 Andrii Pryladyshev.
 * Licensed under the terms defined in the file 'LICENSE.txt',
 * located in the root directory of this project.
 */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {

        val view = LayoutInflater.from(ContextThemeWrapper(parent.context, R.style.Theme_MoreOverlays))
            .inflate(R.layout.accessibility_apps_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val currentItem = getItem(position)

        holder.appIcon.setImageBitmap(currentItem.icon)
        holder.itemView.setBackgroundColor(Color.TRANSPARENT)

        val currentItemInAppData = AppData(currentItem.packageName, currentItem.appName)

        holder.itemView.setOnClickListener {
            onItemClicked(currentItemInAppData)
        }
    }
}