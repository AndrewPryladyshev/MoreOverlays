package com.example.moreoverlays.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.Apps
import com.example.moreoverlays.Notes
import com.example.moreoverlays.R
import com.example.moreoverlays.Widgets
import com.example.moreoverlays.adapters.AppsListAdapter
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.database.ConfigsRepository
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.utils.CATCHER_OVERLAY
import com.example.moreoverlays.utils.DOWN_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.DOWN_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.LEFT_SIDE
import com.example.moreoverlays.utils.LEFT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.MAIN_OVERLAY_LEFT
import com.example.moreoverlays.utils.MAIN_OVERLAY_RIGHT
import com.example.moreoverlays.utils.RIGHT_SIDE
import com.example.moreoverlays.utils.RIGHT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.UP_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.UP_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.createAppData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.atan2

class MyAccessibilityService : AccessibilityService() {
    private lateinit var windowManager: WindowManager
    private var startX = 0f
    private var startY = 0f
    private var isActive: Boolean = false
    private var overlayList: List<OverlayConfig> = emptyList()
    private val overlayViewsMap = mutableMapOf<Int, View>()
    private var appsList = listOf<AppData>()
    private lateinit var selectedApps: MutableList<AppData>
    private lateinit var db: AppDatabase
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var configsRepository: ConfigsRepository

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

//        val sharedPrefs = getSharedPreferences("global_prefs", Context.MODE_PRIVATE)
//        val json = sharedPrefs.getString("overlay_list", null)
        //selectedApps = sharedPrefs.getStringSet("selectedApps", null)?.toMutableSet() ?: mutableSetOf()
        //Log.i("SelectedApps: ", "$selectedApps")
        // appsList = getInstalledApps(this)

        db = AppDatabase.getInstance(applicationContext)
        configsRepository = ConfigsRepository(db.daoOverlayConfigs())

//        serviceScope.launch {
//            selectedApps = configsRepository.getAll()
//            launch(Dispatchers.Main) {
//                val appsView = createAppsView(this@MyAccessibilityService, selectedApps)
//                setOverlayContent(LEFT_SWIPE_OVERLAY, appsView)
//            }
//        }
        serviceScope.launch(Dispatchers.IO) {
            val overlaysFlow = configsRepository.getAll()

            overlaysFlow.collect { newOverlayList ->

                overlayList = newOverlayList

                launch(Dispatchers.Main) {
                    if (overlayList.isNotEmpty()) {
                        createAllOverlays()

                        val leftConfig = overlayList.find { it.id == MAIN_OVERLAY_LEFT }
                        val rightConfig = overlayList.find { it.id == MAIN_OVERLAY_RIGHT }

                        val isLeftEnabled = leftConfig?.isEnabled == true
                        val isRightEnabled = rightConfig?.isEnabled == true

                        Log.d("DEBUG_MINES", "Left Config Enabled: $isLeftEnabled")
                        Log.d("DEBUG_MINES", "Right Config Enabled: $isRightEnabled")

                        if (isLeftEnabled) {
                            showOverlayById(MAIN_OVERLAY_LEFT)
                        } else {
                            hideOverlayById(MAIN_OVERLAY_LEFT)
                        }

                        if (isRightEnabled) {
                            showOverlayById(MAIN_OVERLAY_RIGHT)
                        } else {
                            hideOverlayById(MAIN_OVERLAY_RIGHT)
                        }

                    } else {
                        show("No overlay data found in Database")
                    }
                }
            }
        }
    }


    override fun onInterrupt() {
        Log.d("GestureService", "Service interrupted")
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }


    // HANDLE ALL TOUCH EVENTS
    private fun handleTouch(event: MotionEvent?, id: Int) {
        val rawX = event?.rawX
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
                    MAIN_OVERLAY_LEFT -> {
                        recognizeGesture(rawX!!, rawY!!, id)
                    }
                    MAIN_OVERLAY_RIGHT -> {
                        recognizeGesture(rawX!!, rawY!!, id)
                    }

                    CATCHER_OVERLAY -> {
                        if (isActive) {
                            if (isConfigEnabled(MAIN_OVERLAY_LEFT)) showOverlayById(MAIN_OVERLAY_LEFT)
                            if (isConfigEnabled(MAIN_OVERLAY_RIGHT)) showOverlayById(MAIN_OVERLAY_RIGHT)
                            hideOverlaysExcept(MAIN_OVERLAY_LEFT)
                            hideOverlaysExcept(MAIN_OVERLAY_RIGHT)
                        }
                    }
                    else -> {

                        }
                }

            }

        }

    }

    // WHICH GESTURE WAS MADE
    private fun recognizeGesture(endX: Float, endY: Float, id: Int) {
        val dx = endX - startX
        val dy = endY - startY
        val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))


        if (id == MAIN_OVERLAY_RIGHT) {
            when {

                angle in 90.0..150.0 -> {
                    show("↙ Down Left Swipe ")
                    hideOverlayById(MAIN_OVERLAY_RIGHT)
                    hideOverlayById(MAIN_OVERLAY_LEFT)
                    displayOverlayContent(DOWN_SWIPE_RIGHT_SIDE_OVERLAY)
                    showOverlayById(DOWN_SWIPE_RIGHT_SIDE_OVERLAY)
                    showOverlayById(CATCHER_OVERLAY)
                    overlayViewsMap[CATCHER_OVERLAY]?.setBackgroundColor(Color.TRANSPARENT)
                    isActive = true
                }

                angle in -150.0..-90.0 -> {
                    show("↖ Up Left Swipe")
                    hideOverlayById(MAIN_OVERLAY_RIGHT)
                    hideOverlayById(MAIN_OVERLAY_LEFT)
                    displayOverlayContent(UP_SWIPE_RIGHT_SIDE_OVERLAY)
                    showOverlayById(UP_SWIPE_RIGHT_SIDE_OVERLAY)
                    showOverlayById(CATCHER_OVERLAY)
                    overlayViewsMap[CATCHER_OVERLAY]?.setBackgroundColor(Color.TRANSPARENT)
                    isActive = true
                }
                // LEFT SWIPE
                angle > 150.0 || angle < -150.0 -> {
                    show("← Left Swipe")
                    hideOverlayById(MAIN_OVERLAY_RIGHT)
                    hideOverlayById(MAIN_OVERLAY_LEFT)
                    displayOverlayContent(LEFT_SWIPE_OVERLAY)
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

        } else if (id == MAIN_OVERLAY_LEFT) {
            when (angle) {
                in -90.0..-30.0 -> {
                    show("↗ Up Right Swipe ")
                    hideOverlayById(MAIN_OVERLAY_RIGHT)
                    hideOverlayById(MAIN_OVERLAY_LEFT)
                    displayOverlayContent(UP_SWIPE_LEFT_SIDE_OVERLAY)
                    showOverlayById(UP_SWIPE_LEFT_SIDE_OVERLAY)
                    showOverlayById(CATCHER_OVERLAY)
                    overlayViewsMap[CATCHER_OVERLAY]?.setBackgroundColor(Color.TRANSPARENT)
                    isActive = true
                }

                in -30.0..30.0 -> {
                    show("→ Right Swipe")
                    hideOverlayById(MAIN_OVERLAY_RIGHT)
                    hideOverlayById(MAIN_OVERLAY_LEFT)
                    displayOverlayContent(RIGHT_SWIPE_OVERLAY)
                    showOverlayById(RIGHT_SWIPE_OVERLAY)
                    showOverlayById(CATCHER_OVERLAY)
                    overlayViewsMap[CATCHER_OVERLAY]?.setBackgroundColor(Color.TRANSPARENT)
                    isActive = true

                }

                in 30.0..90.0 -> {
                    show("↘ Down Right Swipe")
                    hideOverlayById(MAIN_OVERLAY_RIGHT)
                    hideOverlayById(MAIN_OVERLAY_LEFT)
                    displayOverlayContent(DOWN_SWIPE_LEFT_SIDE_OVERLAY)
                    showOverlayById(DOWN_SWIPE_LEFT_SIDE_OVERLAY)
                    showOverlayById(CATCHER_OVERLAY)
                    overlayViewsMap[CATCHER_OVERLAY]?.setBackgroundColor(Color.TRANSPARENT)
                    isActive = true
                }

                else -> {
                    Log.d("GestureService", "Unrecognized gesture with angle: $angle")
                    show("IDK WTF IS THAT BITCH")
                }
            }

        }

    }


    private fun show(text: String) {
        Log.d("GestureService", text)
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }


    // GET LIST WITH OVERLAYS PARAMS FROM OTHER ACTIVITY AND CREATE OVERLAYS FOR EACH ONE
    private fun createAllOverlays() {

        overlayViewsMap.values.forEach { view ->
            try {
                windowManager.removeView(view)
            } catch (e: Exception) {
                Log.e("Service", "Error removing view: ${e.message}")
            }
        }
        overlayViewsMap.clear()

        overlayList.forEach { item ->
            val overlayView = item.createOverlay(this)
            overlayView.visibility = View.GONE
            overlayView.id = item.id
            val params: WindowManager.LayoutParams


            when (item.id) {
                MAIN_OVERLAY_RIGHT -> {
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

                MAIN_OVERLAY_LEFT -> {
                    params = WindowManager.LayoutParams(
                        item.width,
                        item.height,
                        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT
                    ).apply {
                        gravity = Gravity.BOTTOM or Gravity.START
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
                        gravity = if (item.side == RIGHT_SIDE) {
                            Gravity.BOTTOM or Gravity.END
                        } else {
                            Gravity.BOTTOM or Gravity.START
                        }

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

    private fun createAppsView(context: Context, apps: List<AppData>, side: Int): View {

//        val layout = LinearLayout(context).apply {
//            orientation = LinearLayout.VERTICAL
//            setBackgroundResource(R.drawable.right_side_overlay_bg)
//            setPadding(32, 32, 32, 32)
//        }

        val layout = LayoutInflater.from(applicationContext).inflate(R.layout.apps_overlay_layout, null)
        val rv: RecyclerView = layout.findViewById(R.id.rv)
        val bgResource = when (side) {
            RIGHT_SIDE -> {
                R.drawable.right_side_overlay_bg
            }
            LEFT_SIDE -> {
                R.drawable.left_side_overlay_bg
            }

            else -> { null }
        }

        if (bgResource != null) {
            rv.setBackgroundResource(bgResource)
        }

        val appsList = createAppData(apps, applicationContext)

        val adapter = AppsListAdapter(onItemClicked = { _, appData ->
            val intent = context.packageManager.getLaunchIntentForPackage(appData.appPackage)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            hideOverlaysExcept(MAIN_OVERLAY_LEFT)
            hideOverlaysExcept(MAIN_OVERLAY_RIGHT)
            if (isConfigEnabled(MAIN_OVERLAY_LEFT)) showOverlayById(MAIN_OVERLAY_LEFT)
            if (isConfigEnabled(MAIN_OVERLAY_RIGHT)) showOverlayById(MAIN_OVERLAY_RIGHT)
        })
        adapter.isClickable = true

        rv.adapter = adapter
        adapter.submitList(appsList)
        rv.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.VERTICAL
            LayoutParams.WRAP_CONTENT
            LayoutParams.WRAP_CONTENT

        }

//        for (app in apps) {
//            val icon = ImageView(context).apply {
//                setImageDrawable(context.packageManager.getApplicationIcon(app.appPackage))
////                layoutParams = LinearLayout.LayoutParams(150, 150).apply {
////                    //marginEnd = 16
////                    setBackgroundColor(Color.TRANSPARENT)
////                }
//                setOnClickListener {
//                    val intent = context.packageManager.getLaunchIntentForPackage(app.appPackage)
//                    intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    context.startActivity(intent)
//                    hideOverlaysExcept(MAIN_OVERLAY_LEFT)
//                    hideOverlaysExcept(MAIN_OVERLAY_RIGHT)
//                    if (isConfigEnabled(MAIN_OVERLAY_LEFT)) showOverlayById(MAIN_OVERLAY_LEFT)
//                    if (isConfigEnabled(MAIN_OVERLAY_RIGHT)) showOverlayById(MAIN_OVERLAY_RIGHT)
//                }
//                scaleType = ImageView.ScaleType.CENTER_INSIDE
//                scaleX = 1f
//                scaleY = 1f
//            }
//            layout.addView(icon)
//        }

        return layout
    }

    // set new content on overlay
    private fun setOverlayContent(overlayId: Int, newContent: View) {
        val container = overlayViewsMap[overlayId] as? ViewGroup ?: return
//        val catcherOverlay = overlayViewsMap[CATCHER_OVERLAY] as? ViewGroup ?: return
        container.removeAllViews()
        container.addView(newContent)
    }

    private fun displayOverlayContent(overlayId: Int) {
        val config = overlayList.find { it.id == overlayId }
        if (config == null) {
            Log.e("Service", "Config not found for overlay ID: $overlayId")
            return
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            LayoutParams.WRAP_CONTENT
            LayoutParams.WRAP_CONTENT
            setBackgroundColor(Color.TRANSPARENT)
        }

        config.contentTypes.forEach { item ->
            when (item) {
                is Apps -> {
                    val appsView = createAppsView(this, item.apps, config.side)
                    rootLayout.addView(appsView)
                }
                is Notes -> {

                }
                is Widgets -> {

                }

            }
        }

        setOverlayContent(overlayId, rootLayout)
    }

    private fun showOverlayById(id: Int) {
        overlayViewsMap[id]?.visibility = View.VISIBLE
        if (id == MAIN_OVERLAY_RIGHT || id == MAIN_OVERLAY_LEFT) {
            overlayViewsMap[id]?.setBackgroundColor(Color.DKGRAY)
        } else overlayViewsMap[id]?.setBackgroundColor(Color.TRANSPARENT)
    }


    private fun hideOverlayById(id: Int) {
        overlayViewsMap[id]?.visibility = View.GONE
    }


    private fun hideOverlaysExcept(id: Int) {
        overlayViewsMap.forEach { (overlayId, view) ->
            if (overlayId != id && overlayId != MAIN_OVERLAY_RIGHT && overlayId != MAIN_OVERLAY_LEFT) {
                view.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun isConfigEnabled(id: Int): Boolean {
        return overlayList.find { it.id == id }?.isEnabled == true
    }
}
