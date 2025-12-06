package com.example.moreoverlays.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.moreoverlays.America

class AppsDiffCallback : DiffUtil.ItemCallback<America>() {

    override fun areItemsTheSame(oldItem: America, newItem: America): Boolean {
        return oldItem.packageName == newItem.packageName
    }

    override fun areContentsTheSame(oldItem: America, newItem: America): Boolean {
        return oldItem == newItem
    }

}