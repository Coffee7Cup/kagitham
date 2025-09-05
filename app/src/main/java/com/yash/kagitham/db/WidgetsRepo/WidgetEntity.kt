package com.yash.kagitham.db.WidgetsRepo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Data class representing a widgetClass entry in the database
 */

@Entity(tableName = "widgets")
data class WidgetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "owner") val owner: String,
    @ColumnInfo(name = "widgetClass") val widgetClass: String,
    @ColumnInfo(name = "apk_path") val apkPath: String,
)