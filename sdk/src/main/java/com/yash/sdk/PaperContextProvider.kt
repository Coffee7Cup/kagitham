package com.yash.sdk

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

/**
 * Provides an isolated Context-like wrapper for each plugin.
 * Handles files, cache, resources, databases, etc.
 */
class PaperContextProvider(
    base: Context,
    private val paperName: String
) : ContextWrapper(base) {

    /** ---------------- Resources & Assets ---------------- */

    private var paperResources: Resources? = null

    init {
        val assetManager = AssetManager::class.java.newInstance()
        val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
        addAssetPath.invoke(assetManager, "${base.filesDir}/plugins/$paperName")

        val superRes = base.resources
        paperResources = Resources(assetManager, superRes.displayMetrics, superRes.configuration)
    }

    override fun getResources(): Resources {
        return paperResources ?: super.getResources()
    }

    /** ---------------- Files & Cache ---------------- */
    override fun getFilesDir(): File {
        val dir = File(baseContext.filesDir, "plugins/$paperName/files")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    override fun getCacheDir(): File {
        val dir = File(baseContext.cacheDir, "plugins/$paperName/cache")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getPluginRootDir(): File {
        val dir = File(baseContext.filesDir, "plugins/$paperName")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /** ---------------- Databases ---------------- */
    override fun getDatabasePath(name: String): File {
        val dbDir = File(getPluginRootDir(), "databases")
        if (!dbDir.exists()) dbDir.mkdirs()
        return File(dbDir, name)
    }

    override fun openOrCreateDatabase(
        name: String,
        mode: Int,
        factory: android.database.sqlite.SQLiteDatabase.CursorFactory?
    ): android.database.sqlite.SQLiteDatabase {
        return android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory)
    }

    /**
     * Create a Room database scoped to this plugin.
     * Each plugin gets its own DB file inside its sandbox.
     */
    fun <T : RoomDatabase> buildRoomDatabase(
        dbClass: Class<T>,
        dbName: String
    ): T {
        return Room.databaseBuilder(
            this, // plugin context
            dbClass,
            dbName
        ).build()
    }

}
