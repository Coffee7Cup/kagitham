package com.yash.kagitham.db.PluginRepo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MetaDataPlugin")
data class MetaDataPluginEntity(
    @PrimaryKey val name: String,
    val author: String,
    val version: String,
    val id: String,
    val entryPoint: String,
    val widgets: String,   // ✅ just plain String
    val apiClass: String,
    val apiInterface: String?,
    val path: String
)
