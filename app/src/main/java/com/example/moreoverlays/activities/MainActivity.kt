package com.example.moreoverlays.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.moreoverlays.Apps
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.R
import com.example.moreoverlays.ViewModels.MainActivityViewModel
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.adapters.Parent
import com.example.moreoverlays.database.AppsRepository
import com.example.moreoverlays.database.ConfigsRepository
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.fragments.MainFragment
import com.example.moreoverlays.fragments.OverlaySettingsFragment
import com.example.moreoverlays.fragments.ViewSettingsFragment
import com.example.moreoverlays.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer


class MainActivity : AppCompatActivity() {
 //   private lateinit var recyclerView: RecyclerView
    private var appsList: List<AppData> = emptyList()
    private lateinit var overlaysRecyclerView: RecyclerView
    private val viewModel: MainActivityViewModel by viewModels()

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


        val mainFragment = MainFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, mainFragment)
            commit()
        }

//        recyclerView = findViewById(R.id.recyclerView)
//        val saveButton: Button = findViewById(R.id.save)

//        overlaysRecyclerView = findViewById(R.id.overlaysRecyclerView)
//        overlaysRecyclerView.adapter = MainOverlaysRecyclerViewAdapter(overlayList)

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
            putInt("overlay_id", data.id)
        }
        val overlaySettingsFragment = OverlaySettingsFragment()
        overlaySettingsFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, overlaySettingsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun openViewSettingsFragment(item: ContentTypeData, data: OverlayConfig) {
        val bundle = Bundle().apply {
            putParcelable("overlay_view", item)
            putParcelable("overlay_data", data)
        }
        val viewSettingsFragment = ViewSettingsFragment()
        viewSettingsFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, viewSettingsFragment)
            .addToBackStack(null)
            .commit()

    }

}