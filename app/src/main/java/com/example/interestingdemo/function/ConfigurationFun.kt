package com.example.interestingdemo.function

import android.content.Context
import com.example.interestingdemo.database.ConfigurationData
import com.example.interestingdemo.database.MyDataBase
import kotlinx.coroutines.*

class ConfigurationFun(context: Context) {
    private val configDao = MyDataBase.getDataBase(context)?.configDao()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * 添加或更新配置
     * @param name 配置名称
     * @param status 配置状态
     */
    fun addOrUpdate(name : String, status: Int) {
        val config = getConfig(name)
        if (config ==null){
            insertConfig(ConfigurationData(configName = name,configStatus = status))
        }else{
            updateConfig(config.id ?: 0,status)
        }
    }

    /**
     * 根据名称获得配置
     */
    fun getConfig(name: String) : ConfigurationData? = runBlocking {
        return@runBlocking configDao?.getByConfig(name)
    }

    /**
     * 插入配置
     */
    private fun insertConfig(configurationData: ConfigurationData) = runBlocking {
        configDao?.insert(configurationData)
    }

    /**
     * 根据id更新配置
     */
    private fun updateConfig(id : Int, status : Int) = runBlocking{
        configDao?.updateStatus(id,status)
    }


}