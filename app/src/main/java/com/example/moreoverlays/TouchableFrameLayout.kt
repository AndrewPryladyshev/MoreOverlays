package com.example.moreoverlays

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class TouchableFrameLayout(context: Context) : FrameLayout(context) {
    constructor(context: Context, attrs: AttributeSet?) : this(context)

    override fun performClick(): Boolean {
        super.performClick()
        // Здесь можешь добавить действия при клике, если нужно
        return true
    }
}