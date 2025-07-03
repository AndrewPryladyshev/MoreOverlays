package com.example.moreoverlays.services

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import android.graphics.Color
import android.os.Build
import android.view.Window
import com.example.moreoverlays.OverlayItem
import com.example.moreoverlays.utils.AppInfo
import com.example.moreoverlays.utils.CATCHER_OVERLAY
import com.example.moreoverlays.utils.LEFT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.MAIN_OVERLAY
import com.example.moreoverlays.utils.getInstalledApps
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.atan2

class MyAccessibilityService : AccessibilityService() {
    private lateinit var windowManager: WindowManager
    private var startX = 0f
    private var startY = 0f
    private var isActive: Boolean = false
    private var overlayList = arrayListOf<OverlayItem>()
    private val overlayViewsMap = mutableMapOf<Int, View>()
    private var appsList = listOf<AppInfo>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager


        val sharedPrefs = getSharedPreferences("overlay_prefs", Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("overlay_list", null)

        appsList = getInstalledApps(this)

        if (json != null) {
            val type = object : TypeToken<ArrayList<OverlayItem>>() {}.type
            overlayList = Gson().fromJson(json, type)
            createAllViews()
            showOverlayById(MAIN_OVERLAY)


        } else {
            show("No overlay data found")
        }
    }


    override fun onInterrupt() {
        Log.d("GestureService", "Service interrupted")
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }


    // HANDLE ALL TOUCH EVENTS
    private fun handleTouch(event: MotionEvent?, id: Int) {
        val rawX = event?.rawX // screen global coordinates
        val rawY = event?.rawY

        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = rawX!!
                    startY = rawY!!
                    Log.d("GestureService", "Touch down at: ($rawX, $rawY)")
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.d("GestureService", "Touch move at: ($rawX, $rawY)")
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("GestureService", "Touch up at: ($rawX, $rawY)")
                    if (id == MAIN_OVERLAY) {
                        recognizeGesture(rawX!!, rawY!!)
                    } else if (id == CATCHER_OVERLAY && isActive) {
                        showOverlayById(MAIN_OVERLAY)
                        hideOverlaysExcept(MAIN_OVERLAY)
                    }
                }
                MotionEvent.ACTION_OUTSIDE -> {}

            }
        }
    }


    // WHICH GESTURE WAS MADE
    private fun recognizeGesture(endX: Float, endY: Float) {
        val dx = endX - startX
        val dy = endY - startY
        val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))

        when {
            angle in -22.5..22.5 -> {
                show("→ Right Swipe")
            }
            angle in 112.5..157.5 -> {
                show("↘ Left Down Swipe")
            }
            angle in -157.5..-112.5 -> {
                show("↖ Left Up Swipe")
            }
            angle > 157.5 || angle < -157.5 -> {
                show("← Left Swipe")
                hideOverlayById(MAIN_OVERLAY)
                showOverlayById(LEFT_SWIPE_OVERLAY)
                showOverlayById(CATCHER_OVERLAY)
                overlayViewsMap[CATCHER_OVERLAY]?.setBackgroundColor(Color.TRANSPARENT)
                isActive = true
                // SHOW OVERLAY DEPENDS ON GESTURE
            }
            else -> {
                Log.d("GestureService", "Unrecognized gesture with angle: $angle")
                show("IDK WTF IS THAT")
            }
        }
    }


    private fun show(text: String) {
        Log.d("GestureService", text)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


    // GET LIST WITH OVERLAYS PARAMS FROM OTHER ACTIVITY AND CREATE OVERLAYS FOR EACH ONE
    private fun createAllViews() {

        overlayList.forEach { item ->
            val overlayView = item.createOverlay(this)
            overlayView.visibility = View.GONE
            overlayView.id = item.id

            val params = WindowManager.LayoutParams(
                item.width,
                item.height,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                x = item.x
                y = item.y
            }

            overlayView.setOnTouchListener { view, event ->
                handleTouch(event, view.id)
                if (view.id != CATCHER_OVERLAY) {
                    view.performClick()
                } else false
            }

            windowManager.addView(overlayView, params)
            overlayViewsMap[item.id] = overlayView
        }

    }


    private fun showOverlayById(id: Int) {
        overlayViewsMap[id]?.visibility = View.VISIBLE
        overlayViewsMap[id]?.setBackgroundColor(Color.WHITE)
    }


    private fun hideOverlayById(id: Int) {
        overlayViewsMap[id]?.visibility = View.GONE
    }


    private fun hideOverlaysExcept(id: Int) {
        overlayViewsMap.forEach { (overlayId, view) ->
            if (overlayId != id) {
                view.visibility = View.GONE
            }
        }
    }

}
