package com.yash.kagitham.utils

import com.yash.kagitham.db.ApisRepo.ApiEntity
import com.yash.kagitham.db.PluginRepo.MetaDataPluginEntity
import com.yash.kagitham.db.WidgetsRepo.WidgetEntity
import com.yash.sdk.loadDex

object PaperInstanceRegistry {
    val mapOfInstances = mutableMapOf<String, Any>()

    fun getPaperInstance(plugin: MetaDataPluginEntity) : Any{
        return mapOfInstances.getOrPut(plugin.name){
             loadDex(plugin.path,plugin.entryPoint)
        }
    }

    fun getWidgetInstance(widget: WidgetEntity) : Any{
        return mapOfInstances.getOrPut(widget.id){
            loadDex(widget.apkPath,widget.widgetClass)
        }
    }

    fun getApiClassInstance(api : ApiEntity) : Any{
        return mapOfInstances.getOrPut(api.id){
            loadDex(api.apkPath,api.apiClass)
        }
    }

}