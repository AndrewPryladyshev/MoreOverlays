package com.example.moreoverlays.services

import android.accessibilityservice.AccessibilityService
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
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.room.Dao
import androidx.room.Database
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.utils.CATCHER_OVERLAY
import com.example.moreoverlays.utils.LEFT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.MAIN_OVERLAY
import com.example.moreoverlays.utils.getInstalledApps
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.math.atan2

class MyAccessibilityService : AccessibilityService() {
    private lateinit var windowManager: WindowManager
    private var startX = 0f
    private var startY = 0f
    private var isActive: Boolean = false
    private var overlayList = arrayListOf<OverlayConfig>()
    private val overlayViewsMap = mutableMapOf<Int, View>()
    private var appsList = listOf<AppData>()
    private lateinit var selectedApps: MutableList<AppData>
    private lateinit var db: AppDatabase
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val sharedPrefs = getSharedPreferences("global_prefs", Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("overlay_list", null)
        //selectedApps = sharedPrefs.getStringSet("selectedApps", null)?.toMutableSet() ?: mutableSetOf()
        //Log.i("SelectedApps: ", "$selectedApps")
        // appsList = getInstalledApps(this)

        serviceScope.launch {
            val appsDao = AppDatabase.getInstance(applicationContext).daoApps()

            selectedApps = appsDao.getSelectedApps()
            launch(Dispatchers.Main) {
                val appsView = createAppsView(this@MyAccessibilityService, selectedApps)
                setOverlayContent(LEFT_SWIPE_OVERLAY, appsView)
            }
        }

        if (json != null) {
            val jsonParser = Json { ignoreUnknownKeys = true }
            overlayList = jsonParser.decodeFromString<ArrayList<OverlayConfig>>(json)
            createAllOverlays()
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


        when (event?.action) {
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

                when (id) {
                    MAIN_OVERLAY -> {
                        recognizeGesture(rawX!!, rawY!!)
                    }

                    CATCHER_OVERLAY -> {
                        if (isActive) {
                            showOverlayById(MAIN_OVERLAY)
                            hideOverlaysExcept(MAIN_OVERLAY)
                        }
                    }
                    else -> {

                        }
                }

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
            // LEFT SWIPE
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
                show("IDK WTF IS THAT BITCH")
            }
        }
    }


    private fun show(text: String) {
        Log.d("GestureService", text)
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


    // GET LIST WITH OVERLAYS PARAMS FROM OTHER ACTIVITY AND CREATE OVERLAYS FOR EACH ONE
    private fun createAllOverlays() {

        overlayList.forEach { item ->
            val overlayView = item.createOverlay(this)
            overlayView.visibility = View.GONE
            overlayView.id = item.id
            val params: WindowManager.LayoutParams


            when (item.id) {
                MAIN_OVERLAY -> {
                    params = WindowManager.LayoutParams(
                        item.width,
                        item.height,
                        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.END
                        x = item.x
                        y = item.y
                    }
                }
                CATCHER_OVERLAY -> {
                    params = WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.END
                        x = item.x
                        y = item.y
                    }
                }
                else -> {
                    params = WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.END
                        x = item.x
                        y = item.y
                    }
                }
            }

            overlayView.setOnTouchListener { view, event ->
                handleTouch(event, view.id)
                if (view.id != CATCHER_OVERLAY) view.performClick() else false
            }

            windowManager.addView(overlayView, params)
            overlayViewsMap[item.id] = overlayView

        }
    }

    private fun createAppsView(context: Context, apps: List<AppData>): View {

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(32, 32, 32, 32)
        }

        for (app in apps) {
            val icon = ImageView(context).apply {
                setImageDrawable(context.packageManager.getApplicationIcon(app.appPackage))
                layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                    //marginEnd = 16
                }
                setOnClickListener {
                    val intent = context.packageManager.getLaunchIntentForPackage(app.appPackage)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
            layout.addView(icon)
        }
        return layout
    }

    // set new content on overlay
    // TODO: REWORK THIS FUNCTION FOR RUNTIME VIEWS CHANGING
    private fun setOverlayContent(overlayId: Int, newContent: View) {
        val container = overlayViewsMap[overlayId] as? ViewGroup ?: return
        val catcherOverlay = overlayViewsMap[CATCHER_OVERLAY] as? ViewGroup ?: return
        container.removeAllViews()
        container.addView(newContent)
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

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

}

// TODO: MAKE DISPLAYING APPS ON OVERLAY MORE FLEXIBLE. NOW CHANGE APPS DISPLAY POSSIBLE ONLY IN THE CODE