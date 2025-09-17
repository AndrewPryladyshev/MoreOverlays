package com.example.moreoverlays.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.R
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.adapters.Parent
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.fragments.MainFragment
import com.example.moreoverlays.fragments.OverlaySettingsFragment
import com.example.moreoverlays.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
 //   private lateinit var recyclerView: RecyclerView
    private var appsList: List<AppData> = emptyList()
    private lateinit var overlaysRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
        val overlayList = arrayListOf(
            OverlayConfig(CATCHER_OVERLAY, "",WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 0, 0, listOf()),
            OverlayConfig(MAIN_OVERLAY, "",40, 500, 0, 100, listOf()),
            OverlayConfig(DOWN_SWIPE_RIGHT_SIDE_OVERLAY, "Right Down Swipe Overlay", 40, 500, 0, 100, listOf()),
            OverlayConfig(LEFT_SWIPE_OVERLAY, "Left Swipe Overlay",500, 500, 100, 500, listOf()),
            OverlayConfig(UP_SWIPE_RIGHT_SIDE_OVERLAY, "Right Up Swipe Overlay", 40, 500, 0, 100, listOf(ContentTypeData.Apps(1, listOf()))),
            OverlayConfig(DOWN_SWIPE_LEFT_SIDE_OVERLAY, "Left Down Swipe Overlay",40, 500, 0, 100, listOf()),
            OverlayConfig(RIGHT_SWIPE_OVERLAY, "Right Swipe Overlay",40, 500, 0, 100, listOf()),
            OverlayConfig(UP_SWIPE_LEFT_SIDE_OVERLAY, "Left Up Swipe Overlay",40, 500, 0, 100, listOf()),
        )

//        recyclerView = findViewById(R.id.recyclerView)
//        val saveButton: Button = findViewById(R.id.save)

//        overlaysRecyclerView = findViewById(R.id.overlaysRecyclerView)
//        overlaysRecyclerView.adapter = MainOverlaysRecyclerViewAdapter(overlayList)

        val sharedPrefs = getSharedPreferences("global_prefs", MODE_PRIVATE)
        val json = Gson().toJson(overlayList)
        sharedPrefs.edit()
            .putString("overlay_list", json)
            .putBoolean("isFirstLaunch", true)
            .apply()

        val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)

        appsList = getInstalledApps(this)

        lifecycleScope.launch(Dispatchers.IO) {
            if (isFirstLaunch) {
                try {
                    val db = AppDatabase.getInstance(applicationContext)

                    val appDao = db.daoApps()
                    val configsDao = db.daoOverlayConfigs()

                    appDao.insertAll(appsList)
                    configsDao.insertAllConfigs(overlayList)

                    sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        val parentList = listOf(
            Parent("Apps", appsList, false),

            )

        val mainFragment = MainFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, mainFragment)
            commit()
        }

//        recyclerView.apply {
//            isNestedScrollingEnabled= false
//            adapter = ParentRecyclerViewAdapter(parentList)
//        }
//
//        recyclerView.layoutManager = object : LinearLayoutManager(this) {
//            override fun canScrollVertically(): Boolean = false
//        }


//        saveButton.setOnClickListener {
//            val selectedApps: MutableList<AppData> = ChildRecyclerViewAdapter(listOf()).selectedApps
//            val packages = selectedApps.map { it.appPackage }.toSet()
//            sharedPrefs.edit().putStringSet("selectedApps", packages).apply()
//        }

    }

    fun openOverlaySettingsFragment(data: OverlayConfig) {
        val bundle = Bundle().apply {
            putParcelable("overlay_item", data)
        }
        val secondFragment = OverlaySettingsFragment()
        secondFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, secondFragment)
            .addToBackStack(null)
            .commit()
    }

}