package com.yash.kagitham.db.ApisRepo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("api_db")
data class ApiEntity (
    @PrimaryKey(autoGenerate = true) val id : String,
    val owner : String,
    val apiClass : String,
    val apkPath : String
)
