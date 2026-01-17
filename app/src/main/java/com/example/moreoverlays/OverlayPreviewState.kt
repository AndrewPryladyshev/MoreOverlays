package com.example.moreoverlays

data class OverlayPreviewState(
    val id: Int,
    var displayMode: String = "right",
    val overlayIds: List<Int>,
    val opacity: Float,

)
