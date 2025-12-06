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
import com.example.moreoverlays.Widgets
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.database.AppsRepository
import com.example.moreoverlays.database.ConfigsRepository
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.utils.CATCHER_OVERLAY
import com.example.moreoverlays.utils.DOWN_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.DOWN_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.LEFT_SIDE
import com.example.moreoverlays.utils.LEFT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.MAIN_OVERLAY
import com.example.moreoverlays.utils.NOTHING
import com.example.moreoverlays.utils.RIGHT_SIDE
import com.example.moreoverlays.utils.RIGHT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.UP_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.UP_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.getInstalledApps
import com.example.moreoverlays.utils.createAppData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    private val _overlayConfigs = MutableStateFlow<List<OverlayConfig>>(emptyList())
    val overlayConfigs: StateFlow<List<OverlayConfig>> = _overlayConfigs

    init {
        loadData()
        viewModelScope.launch(Dispatchers.IO) {
            configsRepository.getAll().collect { configs ->
                _overlayConfigs.value = configs
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val isFirstLaunch = sharedPrefs.getBoolean("isFirstLaunch", true)
            val allInstalledApps = getInstalledApps(getApplication())

            val americaList = createAppData(allInstalledApps, getApplication())

            _installedApps.value = allInstalledApps
            _americaInstalledApps.value = americaList


            if (isFirstLaunch) {
                val defaultOverlays = createDefaultOverlays()

                try {
                    appsRepository.insert(allInstalledApps)
                    configsRepository.insert(defaultOverlays)

                    withContext(Dispatchers.Main) {
                        sharedPrefs.edit().putBoolean("isFirstLaunch", false).apply()
                    }

                    configsRepository.getAll().first().forEach {
                        { Log.d("DBCheck", "Overlay in DB: $it") }
                    }

                    } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }



    private fun createDefaultOverlays() : List<OverlayConfig> {
        return listOf(
            OverlayConfig(CATCHER_OVERLAY, "", WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 0, 0, mutableListOf(), NOTHING),
            OverlayConfig(MAIN_OVERLAY, "",40, 350, 0, 1000, mutableListOf(), NOTHING),
            OverlayConfig(DOWN_SWIPE_RIGHT_SIDE_OVERLAY, "Right Down Swipe Overlay", 40, 500, 0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), RIGHT_SIDE),
            OverlayConfig(LEFT_SWIPE_OVERLAY, "Left Swipe Overlay",500, 500, 100, 500, mutableListOf(Apps(0, mutableListOf(),"name")), RIGHT_SIDE),
            OverlayConfig(UP_SWIPE_RIGHT_SIDE_OVERLAY, "Right Up Swipe Overlay", 40, 500, 0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), RIGHT_SIDE),
            OverlayConfig(DOWN_SWIPE_LEFT_SIDE_OVERLAY, "Left Down Swipe Overlay",40, 500, 0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), LEFT_SIDE),
            OverlayConfig(RIGHT_SWIPE_OVERLAY, "Right Swipe Overlay",40, 500, 0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), LEFT_SIDE),
            OverlayConfig(UP_SWIPE_LEFT_SIDE_OVERLAY, "Left Up Swipe Overlay",40, 500, 0, 100, mutableListOf(Apps(0, mutableListOf(),"name")), LEFT_SIDE),
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
                        else -> 0
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
}