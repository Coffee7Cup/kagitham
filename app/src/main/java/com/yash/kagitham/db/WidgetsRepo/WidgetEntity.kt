package com.yash.kagitham.db.WidgetsRepo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a widgetClass entry in the database
 */

@Entity(tableName = "widgets")
data class WidgetEntity(
    @PrimaryKey(autoGenerate = true) val id: String,
    @ColumnInfo(name = "owner") val owner: String,
    @ColumnInfo(name = "widgetClass") val widgetClass: String,
    @ColumnInfo(name = "apk_path") val apkPath: String,
)