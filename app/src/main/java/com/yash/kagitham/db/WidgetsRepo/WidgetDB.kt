package com.yash.kagitham.db.WidgetsRepo

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yash.sdk.AppRegistry

@Database(
    entities = [WidgetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WidgetDB : RoomDatabase() {

    abstract fun widgetDao(): WidgetDao

    companion object {
        @Volatile
        private var INSTANCE: WidgetDB? = null

        fun getDatabase(): WidgetDB {
            val context = AppRegistry.getAppContext()

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    WidgetDB::class.java,
                    "widget_database"
                )
                    .fallbackToDestructiveMigration() // optional: wipes db on schema change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
