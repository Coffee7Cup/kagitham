package com.yash.kagitham.db.PluginRepo

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yash.sdk.AppRegistry

@Database(entities = [MetaDataPluginEntity::class], version = 1, exportSchema = false)
abstract class MetaDataPluginDB : RoomDatabase() {
    abstract fun metaDataPluginDao(): MetaDataPluginDao

    companion object {
        @Volatile
        private var INSTANCE: MetaDataPluginDB? = null

        fun getDatabase(): MetaDataPluginDB {
            val context = AppRegistry.getAppContext()
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MetaDataPluginDB::class.java,
                    "metaDataPluginDB"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
