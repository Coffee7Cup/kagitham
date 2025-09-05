package com.yash.sdk

import android.content.Context

/**
 * Keeps track of all plugin contexts.
 * Provides cached instances if already created, otherwise builds a new one.
 */
object ContextRegistry {

    // In-memory cache of plugin contexts
    private val contextMap = mutableMapOf<String, PaperContextProvider>()

    /**
     * Get or create a PaperContextProvider for a given plugin name.
     *
     * @param base Base app context
     * @param pluginName Unique plugin name (same as in DB/metaData)
     */
    fun getPluginContext(pluginName: String): PaperContextProvider {
        return contextMap.getOrPut(pluginName) {
            PaperContextProvider(pluginName)
        }
    }

    fun removePluginContext(pluginName: String) {
        contextMap.remove(pluginName)
    }

    fun clearAll() {
        contextMap.clear()
    }


    fun hasContext(pluginName: String): Boolean {
        return contextMap.containsKey(pluginName)
    }
}
