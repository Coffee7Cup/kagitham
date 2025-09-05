package com.yash.kagitham.db.PluginRepo

import com.yash.kagitham.db.WidgetsRepo.WidgetDB
import java.io.File

object PluginRepo {
    private val dao = MetaDataPluginDB.getDatabase().metaDataPluginDao()

    suspend fun delWidgets(pluginName : String){
        val widgetDao = WidgetDB.getDatabase().widgetDao()

        val widgetsOfPlugin = widgetDao.getWidgetsByOwner(pluginName)

        widgetsOfPlugin.forEach {
            widgetDao.delete(it)
        }
    }

    suspend fun deletePlugin(plugin: MetaDataPluginEntity): Boolean {
        // Delete DB entry
        dao.delete(plugin)

        delWidgets(plugin.name)

        // Delete folder from path
        val file = File(plugin.path)
        return if (file.exists()) {
            file.deleteRecursively()  // ✅ removes directory and contents
        } else {
            false
        }
    }

    suspend fun deleteByName(name: String): Boolean {
        val plugins = dao.getAllPlugins()
        val target = plugins.find { it.name == name } ?: return false

        delWidgets(name)

        // Delete from DB
        dao.deleteByName(name)

        // Delete from path
        val file = File(target.path)
        return if (file.exists()) {
            file.deleteRecursively()
        } else {
            false
        }
    }
}