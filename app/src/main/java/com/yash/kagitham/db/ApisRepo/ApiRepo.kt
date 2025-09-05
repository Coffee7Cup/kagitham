package com.yash.kagitham.db.ApisRepo

object ApiRepo {
    private val dao = ApiDB.getDatabase().apiDao()

    suspend fun delApiByPluginName(pluginName : String){
        val apis = dao.getAllApis()
        val delApi = apis.firstOrNull { it -> it.owner == pluginName }

        if(delApi != null) dao.delete(delApi)
    }
}