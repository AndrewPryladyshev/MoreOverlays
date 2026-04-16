package com.example.moreoverlays.services

import android.accessibilityservice.AccessibilityService
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.accessibility.AccessibilityEvent
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.Apps
import com.example.moreoverlays.R
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.database.ConfigsRepository
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.utils.CATCHER_OVERLAY
import com.example.moreoverlays.utils.DOWN_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.DOWN_SWIPE_RIGHT_SIDE_OVERLAY
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

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var startX = 0f
    private var startY = 0f
    private var overlayList: List<OverlayConfig> = emptyList()
    private val overlayViewsMap = mutableMapOf<Int, View>()
    private val overlayContentMap = mutableMapOf<Int, View>()
    private lateinit var db: AppDatabase
    private val serviceJob = SupervisorJob()
    private lateinit var configsRepository: ConfigsRepository

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("ServiceLifecycle", "✅ onServiceConnected — PID: ${android.os.Process.myPid()}")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

//        val sharedPrefs = getSharedPreferences("global_prefs", Context.MODE_PRIVATE)
//        val json = sharedPrefs.getString("overlay_list", null)
        //selectedApps = sharedPrefs.getStringSet("selectedApps", null)?.toMutableSet() ?: mutableSetOf()
        //Log.i("SelectedApps: ", "$selectedApps")
        // appsList = getInstalledApps(this)

        db = AppDatabase.getInstance(applicationContext)
        configsRepository = ConfigsRepository(db.daoOverlayConfigs())

        serviceScope.launch {

            configsRepository.getAll().collect { newOverlayList ->
//                if (newOverlayList.isEmpty()) return@collect
                Log.d("ServiceLifecycle", "📦 DB emitted list, size=${newOverlayList.size}")
                if (newOverlayList.isNotEmpty()) {
                    overlayList = newOverlayList
                    setupOverlay()
                }
            }
        }
    }


    override fun onInterrupt() {
        Log.d("ServiceLifecycle", "⚠️ onInterrupt called")
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ServiceLifecycle", "💀 onDestroy called — PID: ${android.os.Process.myPid()}")
        serviceJob.cancel()
    }

    private fun setupOverlay() {
        Log.d("ServiceLifecycle", "🔧 setupOverlay called, list size=${overlayList.size}")

        overlayViewsMap.values.forEach { view ->
            try {
                if (view.isAttachedToWindow) {
                    windowManager.removeView(view)
                }
            } catch (e: IllegalArgumentException) {
                Log.e("Service", "View not attached, skip removal")
            }
        }
        overlayViewsMap.clear()
        overlayContentMap.clear()

        overlayList.forEach { config ->
            val root = createRootView(config)

            if (config.id !in listOf(MAIN_OVERLAY_LEFT, MAIN_OVERLAY_RIGHT, CATCHER_OVERLAY)) {
                val content = createContent(config)
                root.addView(content)
            }

            windowManager.addView(root, getLayoutParamsForConfig(config))
            overlayViewsMap[config.id] = root

            if (config.id == MAIN_OVERLAY_LEFT || config.id == MAIN_OVERLAY_RIGHT) {
                root.visibility = if (config.isEnabled) View.VISIBLE else View.GONE
            } else {
                root.visibility = View.GONE
            }
        }

    }


    private fun createRootView(config: OverlayConfig): FrameLayout {
        val layout = FrameLayout(this)
        layout.apply {
            id = config.id
            setBackgroundColor(if (id == MAIN_OVERLAY_LEFT || id == MAIN_OVERLAY_RIGHT) Color.DKGRAY else Color.TRANSPARENT)

            if (config.id in listOf(MAIN_OVERLAY_LEFT, MAIN_OVERLAY_RIGHT, CATCHER_OVERLAY)) {
                setOnTouchListener { view, event ->
                    handleTouch(event, view.id)
                    if (view.id != CATCHER_OVERLAY) view.performClick()
                    true
                }
            }

        }
        if (config.id !in listOf(MAIN_OVERLAY_LEFT, MAIN_OVERLAY_RIGHT, CATCHER_OVERLAY)) {
            layout.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        return layout
    }

    private fun createContent(config: OverlayConfig): View {
        val layout = LayoutInflater.from(this).inflate(R.layout.apps_overlay_layout, null)
        val rv = layout.findViewById<RecyclerView>(R.id.rv)

        val bg = if (config.side == RIGHT_SIDE) R.drawable.right_side_overlay_bg else R.drawable.left_side_overlay_bg
        rv.setBackgroundResource(bg)

        val item = config.contentTypes.filterIsInstance<Apps>().firstOrNull()
        if (item != null) {
            val adapter = AccessibilityAppsListAdapter(onItemClicked = { appData ->
                Log.d("TAG1", "LAUNCHING APP")
                launchApp(appData.appPackage)
            })

            rv.layoutManager = LinearLayoutManager(applicationContext)
            rv.adapter = adapter

            adapter.submitList(createAppData(item.apps, this))
        }
        return layout
    }

    private fun getLayoutParamsForConfig(config: OverlayConfig): LayoutParams {
        val flags = LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL

        val params = LayoutParams().apply {
            width = if (config.id == CATCHER_OVERLAY) LayoutParams.MATCH_PARENT else config.width
            height = if (config.id == CATCHER_OVERLAY) LayoutParams.MATCH_PARENT else config.height
            this.type = LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            this.flags = if (config.id == CATCHER_OVERLAY) LayoutParams.FLAG_NOT_FOCUSABLE else flags
            format = PixelFormat.TRANSLUCENT
            gravity = when (config.id) {
                MAIN_OVERLAY_LEFT -> Gravity.BOTTOM or Gravity.START
                MAIN_OVERLAY_RIGHT -> Gravity.BOTTOM or Gravity.END
                CATCHER_OVERLAY -> Gravity.FILL
                else -> if (config.side == RIGHT_SIDE) Gravity.BOTTOM or Gravity.END else Gravity.BOTTOM or Gravity.START
            }
            x = config.x
            y = config.y
        }
        return params
    }

    private fun launchApp(appPackage: String){
        val intent = packageManager.getLaunchIntentForPackage(appPackage)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        Log.d("TAG", "Launch working")
        startActivity(intent)
        closeAllMenus()
    }

    private fun handleTouch(event: MotionEvent?, id: Int) {
        val rawX = event?.rawX ?: 0f
        val rawY = event?.rawY ?: 0f

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = rawX
                startY = rawY
            }
            MotionEvent.ACTION_UP -> {
                if (id == CATCHER_OVERLAY) {
                    closeAllMenus()
                    overlayContentMap[id]?.visibility = View.GONE
                } else if (id == MAIN_OVERLAY_LEFT || id == MAIN_OVERLAY_RIGHT) {
                    recognizeGesture(rawX, rawY, id)
                }
            }
        }
    }

    private fun recognizeGesture(endX: Float, endY: Float, id: Int) {
        val dx = endX - startX
        val dy = endY - startY
        val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))

        val targetOverlayId = when (id) {
            MAIN_OVERLAY_RIGHT -> when {
                angle in 90.0..150.0 -> DOWN_SWIPE_RIGHT_SIDE_OVERLAY
                angle in -150.0..-90.0 -> UP_SWIPE_RIGHT_SIDE_OVERLAY
                angle > 150.0 || angle < -150.0 -> LEFT_SWIPE_OVERLAY
                else -> null
            }
            MAIN_OVERLAY_LEFT -> when (angle) {
                in -90.0..-30.0 -> UP_SWIPE_LEFT_SIDE_OVERLAY
                in -30.0..30.0 -> RIGHT_SWIPE_OVERLAY
                in 30.0..90.0 -> DOWN_SWIPE_LEFT_SIDE_OVERLAY
                else -> null
            }
            else -> null
        }
        Log.d("TAG", "$targetOverlayId")
        if (targetOverlayId != null) {
            showMenu(targetOverlayId)
        }
    }

    private fun showMenu(id: Int) {

        overlayViewsMap[CATCHER_OVERLAY]?.visibility = View.VISIBLE
        val menuView = overlayViewsMap[id]

        menuView?.let {
            it.visibility = View.VISIBLE
            animateSlideIn(it)
            overlayViewsMap[MAIN_OVERLAY_LEFT]?.visibility = View.GONE
            overlayViewsMap[MAIN_OVERLAY_RIGHT]?.visibility = View.GONE
        }
    }

    private fun closeAllMenus() {
        overlayViewsMap.forEach { (id, view) ->
            if (id == MAIN_OVERLAY_LEFT || id == MAIN_OVERLAY_RIGHT) {
                if (isOverlayEnabled(id)) view.visibility = View.VISIBLE
            }
            else if (id == CATCHER_OVERLAY) {
                view.visibility = View.GONE
            }
            else {
                animateSlideOut(view)
            }
        }
    }

    private fun animateSlideIn(view: View?) {
        if (view != null) {
            val width = view.width.toFloat()
            view.translationX = width
            ObjectAnimator.ofFloat(view, "translationX", width, 0f).apply {
                duration = 200
                interpolator = DecelerateInterpolator()
                start()
            }
        }
    }

    private fun animateSlideOut(view: View) {
        val width = view.width.toFloat()
        view.translationX = width
        ObjectAnimator.ofFloat(view, "translationX", 0f, width).apply {
            duration = 200
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
            start()
        }
    }

    private fun isOverlayEnabled(id: Int): Boolean {
        return overlayList.find { it.id == id }?.isEnabled ?: false
    }
}
