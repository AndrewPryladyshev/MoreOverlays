package com.example.moreoverlays.appearance_settings

data class OverlayPreviewState(
    val id: Int,
    var displayMode: String = "right",
    val overlayIds: List<Int>,
    val opacity: Float,
    val cornerSize: Int = 16,


)
