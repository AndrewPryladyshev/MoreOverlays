package com.example.moreoverlays.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppsDao {
    @Query("""SELECT * FROM apps""")
    fun getAll(): Flow<List<AppData>>

    @Query("""SELECT * FROM apps WHERE appsNames IN (:chosenNames)""")
    fun loadAllByIds(chosenNames: String): List<AppData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(appsData: List<AppData>)

    @Update
    suspend fun updateApps(apps: List<AppData>)

    @Query("""SELECT * FROM apps WHERE is_selected = 1""")
    fun getSelectedApps() : MutableList<AppData>
}


@Dao
interface OverlayConfigsDao {
    @Query("""SELECT * FROM overlay_configs""")
    fun getAll(): Flow<List<OverlayConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllConfigs(overlayConfigs: List<OverlayConfig>)

    @Query("SELECT * FROM overlay_configs WHERE id = :id")
    fun getById(id: Int): OverlayConfig?

    @Update
    suspend fun update(data: List<OverlayConfig>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(overlayConfigs: List<OverlayConfig>)

    @Query("""DELETE FROM overlay_configs""")
    suspend fun clear()
}