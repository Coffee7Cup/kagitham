package com.yash.kagitham.db.ApisRepo

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yash.kagitham.db.PluginRepo.MetaDataPluginDB
import com.yash.sdk.AppRegistry

@Database(entities = [ApiEntity::class], version = 1, exportSchema = false)
abstract class ApiDB : RoomDatabase() {

    abstract fun apiDao() : ApiDao

    companion object{
        @Volatile
        private var INSTANCE : ApiDB? = null

        fun getDatabase() : ApiDB{
            val context = AppRegistry.getAppContext()
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApiDB::class.java,
                    "api_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}