package com.example.moreoverlays.database

import kotlinx.coroutines.flow.Flow

class AppsRepository(private val dao: AppsDao) {
    fun getAll() : Flow<List<AppData>> = dao.getAll()
    suspend fun insert(apps: List<AppData>) = dao.insertAll(apps)
    suspend fun update(apps: List<AppData>) = dao.updateApps(apps)
    suspend fun insertAllByIds(chosenNames: String) = dao.loadAllByIds(chosenNames)
}

class ConfigsRepository(private val dao: OverlayConfigsDao) {
    fun getAll() : Flow<List<OverlayConfig>> = dao.getAll()
    fun getById(id : Int) : OverlayConfig? = dao.getById(id)
    suspend fun insert(configs: List<OverlayConfig>) = dao.insertAllConfigs(configs)
    suspend fun update(configs: OverlayConfig) = dao.update(listOf(configs))
    suspend fun updateVisibilityById(configId: Int, isEnabled: Boolean) = dao.updateVisibilityById(configId, isEnabled)
    suspend fun updatePosition(id: Int, x: Int, y: Int) = dao.updateCoordinates(id, x, y)

}
