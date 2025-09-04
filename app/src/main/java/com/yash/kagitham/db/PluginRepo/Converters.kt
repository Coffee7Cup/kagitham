package com.yash.kagitham.db.PluginRepo

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(data: String?): List<String> {
        return if (data.isNullOrEmpty()) emptyList() else data.split(",")
    }
}
