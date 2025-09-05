package com.yash.kagitham.utils

import com.yash.kagitham.db.ApisRepo.ApiEntity
import com.yash.kagitham.db.PluginRepo.MetaDataPluginEntity
import com.yash.kagitham.db.WidgetsRepo.WidgetEntity
import com.yash.sdk.BaseWidget
import com.yash.sdk.loadDex
import com.yash.sdk.PaperEntryPoint

object PaperInstanceRegistry {
    val mapOfInstances = mutableMapOf<String, Any>()

    fun getPaperInstance(plugin: MetaDataPluginEntity) : PaperEntryPoint{
        return mapOfInstances.getOrPut(plugin.name){
             loadDex(plugin.path,plugin.entryPoint)
        } as PaperEntryPoint
    }

    fun getWidgetInstance(widget: WidgetEntity) : BaseWidget{
        return mapOfInstances.getOrPut(widget.id){
            loadDex(widget.apkPath,widget.widgetClass)
        } as BaseWidget
    }

    fun getApiClassInstance(api : ApiEntity) : Any{
        return mapOfInstances.getOrPut(api.id){
            loadDex(api.apkPath,api.apiClass)
        }
    }

}