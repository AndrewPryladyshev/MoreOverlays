package com.example.moreoverlays.viewModels

import android.app.Application
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moreoverlays.America
import com.example.moreoverlays.Apps
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.Notes
import com.example.moreoverlays.appearance_settings.OverlayPreviewState
import com.example.moreoverlays.Widgets
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.database.AppsRepository
import com.example.moreoverlays.database.ConfigsRepository
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.utils.CATCHER_OVERLAY
import com.example.moreoverlays.utils.DIAGONAL_DOWN
import com.example.moreoverlays.utils.DIAGONAL_UP
import com.example.moreoverlays.utils.DOWN_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.DOWN_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.LEFT_SIDE
import com.example.moreoverlays.utils.LEFT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.MAIN
import com.example.moreoverlays.utils.MAIN_OVERLAY_LEFT
import com.example.moreoverlays.utils.MAIN_OVERLAY_RIGHT
import com.example.moreoverlays.utils.NOTHING
import com.example.moreoverlays.utils.RIGHT_SIDE
import com.example.moreoverlays.utils.RIGHT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.STRAIGHT
import com.example.moreoverlays.utils.UP_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.UP_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.getInstalledApps
import com.example.moreoverlays.utils.createAppData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val appsRepository = AppsRepository(db.daoApps())
    private val configsRepository = ConfigsRepository(db.daoOverlayConfigs())
    private val sharedPrefs = application.getSharedPreferences("global_prefs", Application.MODE_PRIVATE)

    private val _installedApps = MutableStateFlow<List<AppData>>(emptyList())
    val installedApps : StateFlow<List<AppData>> = _installedApps

    private val _americaInstalledApps = MutableStateFlow<List<America>>(emptyList())
    val americaInstalledApps : StateFlow<List<America>> = _americaInstalledApps

    private val _previewOverlayStates = MutableStateFlow<List<OverlayPreviewState>>(emptyList())
    val previewOverlayStates : StateFlow<List<OverlayPreviewState>> = _previewOverlayStates

    val overlayConfigs: StateFlow<List<OverlayConfig>> = configsRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadData()

    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)
            val allInstalledApps = getInstalledApps(getApplication())

            val americaList = createAppData(allInstalledApps, getApplication())
            val previewList = createDefaultOverlayPreviewStates()

            _installedApps.value = allInstalledApps
            _americaInstalledApps.value = americaList
            _previewOverlayStates.value = previewList


            if (isFirstLaunch) {
                try {
                    val defaultOverlays = createDefaultOverlays()
                    appsRepository.insert(allInstalledApps)
                    configsRepository.insert(defaultOverlays)

                    withContext(Dispatchers.Main) {
                        sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()
                    }
                } catch (e: Exception) {
                    Log.e("VM_DEBUG", "Error during first launch data seeding", e)
                }
            }
        }
    }



    private fun createDefaultOverlays() : List<OverlayConfig> {
        return listOf(
            OverlayConfig(CATCHER_OVERLAY, "", WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 0, 0, mutableListOf(), NOTHING),
            OverlayConfig(MAIN_OVERLAY_RIGHT, "",40, 350, 0, 1000, mutableListOf(), NOTHING),
            OverlayConfig(MAIN_OVERLAY_LEFT, "",40, 350, 0, 1000, mutableListOf(), NOTHING),
            OverlayConfig(DOWN_SWIPE_RIGHT_SIDE_OVERLAY, "Right Down Swipe Overlay", WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, 0, mutableListOf(Apps(0, mutableListOf(),"name")), RIGHT_SIDE),
            OverlayConfig(LEFT_SWIPE_OVERLAY, "Left Swipe Overlay", WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, 500, mutableListOf(Apps(0, mutableListOf(),"name")), RIGHT_SIDE),
            OverlayConfig(UP_SWIPE_RIGHT_SIDE_OVERLAY, "Right Up Swipe Overlay", WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), RIGHT_SIDE),
            OverlayConfig(DOWN_SWIPE_LEFT_SIDE_OVERLAY, "Left Down Swipe Overlay",40, 500, 0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), LEFT_SIDE),
            OverlayConfig(RIGHT_SWIPE_OVERLAY, "Right Swipe Overlay",40, 500, 0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), LEFT_SIDE),
            OverlayConfig(UP_SWIPE_LEFT_SIDE_OVERLAY, "Left Up Swipe Overlay",40, 500, 0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), LEFT_SIDE),
        )
    }

    private fun createDefaultOverlayPreviewStates() : List<OverlayPreviewState> {
        return listOf(
            OverlayPreviewState(MAIN, "right", listOf(MAIN_OVERLAY_RIGHT, MAIN_OVERLAY_LEFT), 0.5f),
            OverlayPreviewState(DIAGONAL_UP, "right", listOf(UP_SWIPE_RIGHT_SIDE_OVERLAY, UP_SWIPE_LEFT_SIDE_OVERLAY), 0.5f),
            OverlayPreviewState(STRAIGHT, "right", listOf(RIGHT_SWIPE_OVERLAY, LEFT_SWIPE_OVERLAY), 0.5f),
            OverlayPreviewState(DIAGONAL_DOWN, "right", listOf(DOWN_SWIPE_RIGHT_SIDE_OVERLAY, DOWN_SWIPE_LEFT_SIDE_OVERLAY), 0.5f),
        )
    }


    fun updateOverlayConfig(config: OverlayConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            configsRepository.update(config)

        }
    }

    fun addNewContentTypeToOverlay(configId: Int, title: String, type: String) {
        viewModelScope.launch (Dispatchers.IO) {

            val currentConfig = configsRepository.getById(configId)

            if (currentConfig != null) {
                val contentTypes = currentConfig.contentTypes.toMutableList()

                val maxId = currentConfig.contentTypes.maxOfOrNull {
                    when (it) {
                        is Apps -> it.id
                        is Notes -> it.id
                        is Widgets -> it.id
                    }
                } ?: 0

                val newId = maxId + 1

                val newContentType : ContentTypeData? = when (type) {
                    "Apps" -> Apps(newId, mutableListOf(), title, type)
                    "Notes" -> Notes(newId, title, type)
                    "Widgets" -> Widgets(newId, title, type)
                    else -> null
                }
                if (newContentType != null) {
                    contentTypes.add(newContentType)
                    val updatedConfig = currentConfig.copy(contentTypes = contentTypes.toList())
                    configsRepository.update(updatedConfig)
                }
            }
        }
    }

    fun updateContentTypeOnOverlay(configId: Int, title: String, contentTypeId: Int, action: String) {
        viewModelScope.launch(Dispatchers.IO) {

            val currentConfig = configsRepository.getById(configId)

            if (currentConfig != null) {
                val contentTypes = currentConfig.contentTypes.toMutableList()
                val currentContentType = contentTypes.find { it.id == contentTypeId }

                val newContentType = when (currentContentType) {
                    is Apps -> {
                        currentContentType.copy(title = title)
                    }
                    is Widgets -> {
                        currentContentType.copy(title = title)
                    }
                    is Notes -> {
                        currentContentType.copy(title = title)
                    }

                    null -> TODO()

                }

                val index = contentTypes.indexOf(currentContentType)

                if (action == "update") {
                    contentTypes[index] = newContentType

                    val updatedConfig = currentConfig.copy(contentTypes = contentTypes.toList())
                    configsRepository.update(updatedConfig)
                }
                else if (action == "delete") {
                    contentTypes.removeAt(index)

                    val updatedConfig = currentConfig.copy(contentTypes = contentTypes.toList())
                    configsRepository.update(updatedConfig)
                }
            }
        }
    }

    fun updateOverlayApps(overlayConfig: OverlayConfig, appToToggle: AppData, shouldAdd: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {

            val updatedContentTypes = overlayConfig.contentTypes.map { contentType ->
                if (contentType is Apps) {
                    val currentApps = contentType.apps.toMutableList()

                    if (shouldAdd) {
                        if (!currentApps.any { it.appPackage == appToToggle.appPackage }) {
                            currentApps.add(appToToggle)
                        }
                    } else {
                        currentApps.removeAll { it.appPackage == appToToggle.appPackage }
                    }
                    contentType.copy(apps = currentApps)
                } else {
                    contentType
                }
            }

            val newConfig = overlayConfig.copy(contentTypes = updatedContentTypes)

            configsRepository.update(newConfig)
        }
    }

    suspend fun updateOverlayVisibility(configId: Int, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            configsRepository.updateVisibilityById(configId, isEnabled)
        }
    }

    fun updatePosition(id: Int, x: Int, y: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                configsRepository.updatePosition(id, x, y)
            } catch (e: Exception) {
                Log.e("VM_DEBUG", "Error updating position", e)
            }
        }
    }

    fun updatePreviewSide(previewId: Int, newSide: String) {
        val updatedList = _previewOverlayStates.value.map { opState ->
            if (opState.id == previewId) {
                opState.copy(displayMode = newSide)
            } else {
                opState
            }
        }
        _previewOverlayStates.value = updatedList
    }

    fun updatePreviewOpacity(id: Int, opacity: Float) {
        val updatedList = _previewOverlayStates.value.map { opState ->
            if (opState.id == id) {
                opState.copy(opacity = opacity)
            } else {
                opState
            }
        }
        _previewOverlayStates.value = updatedList
    }

    fun updatePreviewCorners(id: Int, corners: Int) {
        val updatedList = _previewOverlayStates.value.map { opState ->
            if (opState.id == id) {
                opState.copy(cornerSize = corners)
            } else {
                opState
            }
        }
        _previewOverlayStates.value = updatedList
    }

//    NEW WAY TO CHANGE THEME
//    fun setTheme(mode: Int) {
//        // AppCompatDelegate.MODE_NIGHT_NO (1)
//        // AppCompatDelegate.MODE_NIGHT_YES (2)
//        // AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM (-1)
//
//        AppCompatDelegate.setDefaultNightMode(mode)
//        sharedPrefs.edit().putInt("prefs_theme_mode", mode).apply()
//    }
//
//    fun getSavedThemeMode(): Int {
//        return sharedPrefs.getInt("prefs_theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//    }


}