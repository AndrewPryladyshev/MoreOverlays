package com.example.moreoverlays.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.R
import com.example.moreoverlays.viewModels.MainActivityViewModel
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.databinding.ActivityMainBinding
import com.example.moreoverlays.fragments.HandleSettingsFragment
import com.example.moreoverlays.fragments.MainFragment
import com.example.moreoverlays.fragments.OverlaySettingsFragment
import com.example.moreoverlays.fragments.ViewSettingsFragment
import com.example.moreoverlays.services.MyAccessibilityService


class MainActivity : AppCompatActivity() {
 //   private lateinit var recyclerView: RecyclerView

    private val PREFS_NAME = "app_settings"
    private val THEME_KEY = "theme_is_dark"

    private var appsList: List<AppData> = emptyList()
    private lateinit var overlaysRecyclerView: RecyclerView
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)
        setSupportActionBar(binding.toolbar)

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
            replace(R.id.fragmentContainer, mainFragment)
            commit()
        }

        val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isCurrentlyDark = currentMode == Configuration.UI_MODE_NIGHT_YES

        binding.toggleThemeBtn.setOnClickListener {
            val newModeIsDark = !isCurrentlyDark

            val newDelegateMode = if (newModeIsDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            AppCompatDelegate.setDefaultNightMode(newDelegateMode)
            saveThemeMode(newModeIsDark)
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

    fun openViewSettingsFragment(overlayConfigId: Int, contentTypeDataId: Int) {
        val bundle = Bundle().apply {
            putInt("overlayConfigId", overlayConfigId)
            putInt("contentTypeDataId", contentTypeDataId)
        }
        val viewSettingsFragment = ViewSettingsFragment()
        viewSettingsFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, viewSettingsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun openHandleSettingsFragment(side: Int) {
        val bundle = Bundle().apply {
            putInt("side", side)
        }
        val handleFragment = HandleSettingsFragment()
        handleFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, handleFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun saveThemeMode(isDark: Boolean) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(THEME_KEY, isDark)
            .apply()
    }

    private fun getSavedThemeMode(): Int {
        val isDark = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(THEME_KEY, false)

        return if (isDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
    }

}