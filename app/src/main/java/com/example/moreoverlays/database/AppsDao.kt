package com.example.moreoverlays.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppsDao {
    @Query("""SELECT * FROM apps""")
    suspend fun getAll(): List<AppData>

    @Query("""SELECT * FROM apps WHERE appsNames IN (:chosenNames)""")
    suspend fun loadAllByIds(chosenNames: String): List<AppData>

    @Insert
    suspend fun insertAll(appsData: List<AppData>)
}


@Dao
interface OverlayConfigs {
    @Query("""SELECT * FROM overlay_configs""")
    suspend fun getAll(): List<OverlayConfig>

    @Insert
    suspend fun insertAllConfigs(overlayConfigs: List<OverlayConfig>)

    @Query("SELECT * FROM overlay_configs WHERE id = :id")
    suspend fun getById(id: Int): OverlayConfig?

    @Update
    suspend fun update(data: List<OverlayConfig>)
}