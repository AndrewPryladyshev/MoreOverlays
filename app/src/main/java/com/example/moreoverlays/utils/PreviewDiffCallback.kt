package com.example.moreoverlays.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.moreoverlays.OverlayPreviewState
import com.example.moreoverlays.database.OverlayConfig

class PreviewDiffCallback : DiffUtil.ItemCallback<OverlayPreviewState>() {

    override fun areItemsTheSame(oldItem: OverlayPreviewState, newItem: OverlayPreviewState): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OverlayPreviewState, newItem: OverlayPreviewState): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: OverlayPreviewState, newItem: OverlayPreviewState): Any? {
        val diff = mutableSetOf<String>()
        if (oldItem.displayMode != newItem.displayMode) diff.add("SIDE_CHANGED")
        if (oldItem.opacity != newItem.opacity) diff.add("OPACITY_CHANGED")

        return diff.ifEmpty { null }
    }



}