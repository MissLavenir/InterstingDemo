package com.example.interestingdemo.function

import android.content.Context
import android.util.Log
import com.example.interestingdemo.database.ConfigurationData
import com.example.interestingdemo.database.MyDataBase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ConfigurationFun(context: Context): CoroutineScope {
    private val configDao = MyDataBase.getDataBase(context)?.configDao()
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job + CoroutineName("ConfigurationFun")

     /**
     * 添加或更新配置
     * @param name 配置名称
     * @param status 配置状态
     */
    fun addOrUpdate(name : String, status: Int) {
        val config = getConfig(name)
        Log.e("debug","addOrUpdate--"+Thread.currentThread().toString())
        if (config == null){
            insertConfig(ConfigurationData(configName = name,configStatus = status))
        }else{
            updateConfig(config.id ?: 0,status)
        }
    }

    /**
     * 根据名称获得配置
     */
    fun getConfig(name: String) : ConfigurationData? = runBlocking(coroutineContext) {
        Log.e("debug","getConfig--"+Thread.currentThread().toString())
        return@runBlocking configDao?.getByConfig(name)
    }

    /**
     * 插入配置
     */
    private fun insertConfig(configurationData: ConfigurationData) = runBlocking(coroutineContext) {
        Log.e("debug","insertConfig--"+Thread.currentThread().toString())
        configDao?.insert(configurationData)
    }

    /**
     * 根据id更新配置
     */
    private fun updateConfig(id : Int, status : Int) = runBlocking(coroutineContext){
        Log.e("debug","updateConfig--"+Thread.currentThread().toString())
        configDao?.updateStatus(id,status)
    }

    fun onDestroy(){
        if (job.isActive){
            job.cancel()
        }
    }

}