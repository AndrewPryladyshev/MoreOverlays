package com.example.moreoverlays.adapters

import com.example.moreoverlays.database.AppData

data class Parent (
    val text: String,
    val appList: List<AppData>,
    var isExpanded: Boolean,

    )
