package com.example.moreoverlays.external.mytopsheet

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.customview.widget.ViewDragHelper
import com.example.moreoverlays.R

class TopSheetBehavior<V: View>@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<V>(context, attrs) {

    var peekHeight: Int = 400
    private var viewDragHelper: ViewDragHelper? = null

    private var currentTop = Int.MIN_VALUE
    private var canDrag = false
    val yVelMax: Float = 8000f
    val animSpeed: Int = 30
    private var systemTopInset = 0

    private val settlingRunnable = object : Runnable {
        private var lastChild: V? = null

        fun setChild(child: V) { lastChild = child }

        override fun run() {
            val child = lastChild ?: return
            if (viewDragHelper != null && viewDragHelper!!.continueSettling(true)) {
                ViewCompat.postOnAnimation(child, this)
            }
        }
    }

    private fun invalidate(child: V) {
        settlingRunnable.setChild(child)
        ViewCompat.postOnAnimation(child, settlingRunnable)
    }

    private val dragCallback = object : ViewDragHelper.Callback() {
        // defines if we can collapse/drag view or not.
        override fun tryCaptureView(
            child: View,
            pointerId: Int
        ): Boolean {
//            return true // true if we can make changes for any view
            return canDrag
        }

        // defines the drag area
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
//            return super.clampViewPositionVertical(child, top, dy)
            val minTop = -child.height + peekHeight
            val maxTop = 0

            return top.coerceIn(minTop, maxTop)
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return 0
        }

        // mb create something interesting here. calls when child view changes
        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
//            super.onViewPositionChanged(changedView, left, top, dx, dy)
            currentTop = top
        }

        // calls when drag helper released
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
//            super.onViewReleased(releasedChild, xvel, yvel)

            val minTop = -releasedChild.height + peekHeight
            val maxTop = 0



            if (yvel > yVelMax) {
                viewDragHelper?.settleCapturedViewAt(0, maxTop)
            }
            else if (yvel < -yVelMax) {
                viewDragHelper?.settleCapturedViewAt(0, minTop)
            }
            else {

                val targetTop = (releasedChild.top + (yvel / animSpeed)).toInt()
                val clampedTarget = targetTop.coerceIn(minTop, maxTop)

                viewDragHelper?.settleCapturedViewAt(0, clampedTarget)
            }

            invalidate(releasedChild as V)
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return child.height - peekHeight
        }


    }

    // To intercept touch events
    // returns true if should intercept
    // then, if true, onTouchEvent() calls
    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        ev: MotionEvent
    ): Boolean {
        if (viewDragHelper == null) {
            viewDragHelper = ViewDragHelper.create(parent, dragCallback)
        }

        val dragHandle = child.findViewById<View>(R.id.drag_handle)

        val isHit = parent.isPointInChildBounds(dragHandle, ev.x.toInt(), ev.y.toInt())
        canDrag = isHit

        if (!isHit) return false

        return viewDragHelper?.shouldInterceptTouchEvent(ev) ?: false

    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {

        val dragHandle = child.findViewById<View>(R.id.drag_handle)

        if (dragHandle == null) {
            viewDragHelper?.processTouchEvent(ev)
            return true
        }

        val isHit = parent.isPointInChildBounds(dragHandle, ev.x.toInt(), ev.y.toInt())

        val isDragging = viewDragHelper?.viewDragState != ViewDragHelper.STATE_IDLE

        if (isHit || isDragging) {
            viewDragHelper?.processTouchEvent(ev)
        }

        return isHit || isDragging
    }


    // Calls every time the view need to be redrawn(e.g. rotate the phone, moving finger through the screen)
    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        parent.onLayoutChild(child, layoutDirection)

        val dragHandle = child.findViewById<View>(R.id.drag_handle)
        peekHeight = (dragHandle?.height?.plus(100)) ?: 400



        if (currentTop == Int.MIN_VALUE) {
            currentTop = -child.height + peekHeight
        }

        val offset = currentTop - child.top
        ViewCompat.offsetTopAndBottom(child, offset)

        if (viewDragHelper?.continueSettling(true) == true) {
            ViewCompat.postInvalidateOnAnimation(child)
        }

        return true
    }

}