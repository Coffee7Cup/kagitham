package com.yash.sdk

import android.annotation.SuppressLint
import android.content.Context
import androidx.navigation.NavController

/**
 * Global Registry for the SDK.
 * Holds app-wide references (like Application Context).
 * Accessible from any module that depends on :sdk.
 */
object AppRegistry {

    //-------------------- App Context ---------------
    private lateinit var appContext: Context
    @SuppressLint("StaticFieldLeak")
    private lateinit var navController : NavController

    /** Set the global Application Context (call once in Application or MainActivity). */
    fun setAppContext(ctx: Context) {
        appContext = ctx.applicationContext
    }

    /** Get the global Application Context safely. */
    fun getAppContext(): Context {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("AppContext not initialized! Call Registry.setAppContext() first.")
        }
        return appContext
    }

    fun setNavController(navController : NavController){
        this.navController = navController
    }

    fun getNavController() = navController
}
