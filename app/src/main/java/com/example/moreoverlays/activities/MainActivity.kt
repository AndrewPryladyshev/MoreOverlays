/*
 * Copyright (c) 2026 Andrii Pryladyshev.
 *              PROPRIETARY AND NON-COMMERCIAL SOURCE-AVAILABLE LICENSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to view
 * the source code and execute the Software solely for personal, non-commercial,
 * and educational purposes, subject to the following conditions:
 *
 * 1. OWNERSHIP: The Software and all intellectual property rights therein are
 *    and shall remain the sole and exclusive property of Andrii Pryladyshev.
 *
 * 2. RESTRICTIONS:
 *    - COMMERCIAL USE: You may not use the Software, or any portion thereof,
 *      for any commercial purposes, including but not limited to selling,
 *      leasing, or using it as part of a paid service.
 *    - MODIFICATION: You may not modify, adapt, transform, or create
 *      derivative works based upon the Software.
 *    - REDISTRIBUTION: You may not redistribute, publish, or host the
 *      Software on any other public platforms or repositories.
 *
 * 3. COPYRIGHT NOTICE: The above copyright notice and this permission notice
 *    shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.moreoverlays.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.appearance_settings.AdvancedAppearanceSettingsFragment
import com.example.moreoverlays.appearance_settings.AppearanceFragment
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.databinding.ActivityMainBinding
import com.example.moreoverlays.fragments.HandleSettingsFragment
import com.example.moreoverlays.fragments.MainFragment
import com.example.moreoverlays.fragments.ViewSettingsFragment
import com.example.moreoverlays.viewModels.MainActivityViewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    private val PREFS_NAME = "app_settings"
    private val THEME_KEY = "theme_is_dark"

    private var appsList: List<AppData> = emptyList()
    private lateinit var overlaysRecyclerView: RecyclerView

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(getSavedThemeMode())

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        window.statusBarColor = android.graphics.Color.TRANSPARENT
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

        if (savedInstanceState == null) {
            val mainFragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, mainFragment)
                .commit()
        }

        val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        val isCurrentlyDark = currentMode == Configuration.UI_MODE_NIGHT_YES

        binding.toggleThemeBtn.setOnClickListener {
            val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val isCurrentlyDark = currentMode == Configuration.UI_MODE_NIGHT_YES

            val newMode = if (isCurrentlyDark) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }

            AppCompatDelegate.setDefaultNightMode(newMode)
            saveThemeMode(newMode == AppCompatDelegate.MODE_NIGHT_YES)
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
            .setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
            )
            .replace(binding.fragmentContainer.id, viewSettingsFragment)
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
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(binding.fragmentContainer.id, handleFragment)
            .addToBackStack(null)
            .commit()
        binding.toolbar.visibility = View.VISIBLE
    }

    fun openAdvancedSettingsFragment() {
//        val bundle = Bundle().apply {
//            putInt("side", side)
//        }
        val handleFragment = AdvancedAppearanceSettingsFragment()
//        handleFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(binding.fragmentContainer.id, handleFragment)
            .addToBackStack(null)
            .commit()
    }

    fun openAppearanceSettingsFragment() {
        val bundle = Bundle().apply {  }

        val appearanceFragment = AppearanceFragment()
        appearanceFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(binding.fragmentContainer.id, appearanceFragment)
            .addToBackStack(null)
            .commit()

        binding.toolbar.visibility = View.GONE
    }

    private fun saveThemeMode(isDark: Boolean) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit {
                putBoolean(THEME_KEY, isDark)
            }
    }

    private fun getSavedThemeMode(): Int {
        val isDark = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getBoolean(THEME_KEY, true)

        return if (isDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
    }


}