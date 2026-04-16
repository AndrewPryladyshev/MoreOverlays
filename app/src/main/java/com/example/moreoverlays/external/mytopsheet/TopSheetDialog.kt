package com.example.moreoverlays.external.mytopsheet

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.moreoverlays.R

class TopSheetDialog(context: Context) : AppCompatDialog(context, R.style.Theme_Design_TopSheetDialog) {

    private var behavior: TopSheetBehavior<FrameLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun setContentView(layoutResId: Int) {
        val wrappedView = wrapInTopSheet(layoutResId, null, null)
        super.setContentView(wrappedView)
    }

    private fun wrapInTopSheet(layoutResId: Int, view: View?, params: ViewGroup.LayoutParams?): View {

        val coordinator = CoordinatorLayout(context)

        val container = FrameLayout(context).apply {
            id = R.id.design_top_sheet
        }

        val layoutParams = CoordinatorLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val newBehavior = TopSheetBehavior<FrameLayout>(context)
        layoutParams.behavior = newBehavior
        this.behavior = newBehavior

        if (layoutResId != 0) {
            LayoutInflater.from(context).inflate(layoutResId, container, true)
        } else if (view != null) {
            container.addView(view)
        }

        coordinator.addView(container, layoutParams)

        return coordinator
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val window = window ?: return super.dispatchTouchEvent(ev)

        val contentView = findViewById<View>(R.id.content)
            ?: return super.dispatchTouchEvent(ev)

        val outRect = Rect()
        contentView.getGlobalVisibleRect(outRect)

        return if (outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
            super.dispatchTouchEvent(ev)
        } else {
            ownerActivity?.dispatchTouchEvent(ev) ?: false
        }
    }
}