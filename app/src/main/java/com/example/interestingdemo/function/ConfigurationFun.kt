package com.example.interestingdemo.function

import android.content.Context
import com.example.interestingdemo.database.ConfigurationData
import com.example.interestingdemo.database.MyDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ConfigurationFun(context: Context) {
    private val configDao = MyDataBase.getDataBase(context)?.configDao()

    /**
     * 添加或更新配置
     * @param name 配置名称
     * @param status 配置状态
     */
    fun addOrUpdate(name : String, status: Int) = runBlocking{
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
        var config : ConfigurationData?
        withContext(Dispatchers.IO){
           config = configDao?.getByConfig(name)
        }
        return@runBlocking config
    }

    /**
     * 插入配置
     */
    private fun insertConfig(configurationData: ConfigurationData) = runBlocking {
        configDao?.Insert(configurationData)
    }

    /**
     * 根据id更新配置
     */
    private fun updateConfig(id : Int, status : Int) = runBlocking{
        configDao?.updateStatus(id,status)
    }


}