package com.yash.kagitham.db.allWidgets

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a widget entry in the database
 */

@Entity(tableName = "widgets")
data class WidgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "owner") val owner: String,
    @ColumnInfo(name = "widget") val widget: String,
    @ColumnInfo(name = "apk_path") val apkPath: String,
)