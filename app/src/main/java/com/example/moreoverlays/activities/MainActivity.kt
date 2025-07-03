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
import com.example.moreoverlays.OverlayItem
import com.example.moreoverlays.R
import com.example.moreoverlays.utils.*
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
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
            OverlayItem(CATCHER_OVERLAY, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 0, 0),
            OverlayItem(MAIN_OVERLAY, 40, 500, 0, 100),
            OverlayItem(DOWN_RIGHT_SWIPE_OVERLAY, 40, 500, 0, 100),
            OverlayItem(LEFT_SWIPE_OVERLAY, 500, 500, 100, 500),
            OverlayItem(UP_RIGHT_SWIPE_OVERLAY, 40, 500, 0, 100),
            OverlayItem(DOWN_LEFT_SWIPE_OVERLAY, 40, 500, 0, 100),
            OverlayItem(RIGHT_SWIPE_OVERLAY, 40, 500, 0, 100),
            OverlayItem(UP_LEFT_SWIPE_OVERLAY, 40, 500, 0, 100),
        )

        val sharedPrefs = getSharedPreferences("overlay_prefs", MODE_PRIVATE)
        val json = Gson().toJson(overlayList)
        sharedPrefs.edit().putString("overlay_list", json).apply()

    }
}