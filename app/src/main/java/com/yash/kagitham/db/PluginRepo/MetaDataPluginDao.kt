package com.yash.kagitham.db.PluginRepo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MetaDataPluginDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plugin: MetaDataPluginEntity)

    @Update
    suspend fun update(plugin: MetaDataPluginEntity)

    @Delete
    suspend fun delete(plugin: MetaDataPluginEntity)

    @Query("SELECT * FROM MetaDataPlugin")
    suspend fun getAllPlugins(): List<MetaDataPluginEntity>

    // ✅ delete by plugin name
    @Query("DELETE FROM MetaDataPlugin WHERE name = :pluginName")
    suspend fun deleteByName(pluginName: String): Int
}
